package at.phactum.demo.workflow.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.service.CustomerService;
import at.phactum.demo.insurance.service.InsuranceService;
import at.phactum.demo.shared.utils.DataItem;
import at.phactum.demo.shared.utils.DataItemConverter;
import at.phactum.demo.shared.utils.DataItemGroup;
import at.phactum.demo.shared.utils.HashMapConverter;
import at.phactum.demo.shared.utils.SortedMapConverter;
import at.phactum.demo.workflow.dto.CompleteTaskDto;
import at.phactum.demo.workflow.dto.CompleteTaskEvent;
import at.phactum.demo.workflow.dto.TaskDto;
import at.phactum.demo.workflow.dto.TypeDto;
import at.phactum.demo.workflow.persistence.InsuranceAggregate;
import at.phactum.demo.workflow.persistence.InsuranceAggregateRepository;
import io.vanillabp.spi.process.ProcessService;
import io.vanillabp.spi.service.BpmnProcess;
import io.vanillabp.spi.service.TaskEvent;
import io.vanillabp.spi.service.TaskId;
import io.vanillabp.spi.service.WorkflowService;
import io.vanillabp.spi.service.WorkflowTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@WorkflowService(workflowAggregateClass = InsuranceAggregate.class,
        bpmnProcess = @BpmnProcess(bpmnProcessId = "Process_InsuranceContractWorkflow"))
@Slf4j
@RequiredArgsConstructor
@Transactional
public class InsuranceWorkflowService {

    private static final String MODULE_ID = "insurance";
    private static final String MODULE_URL = "http://localhost:8080";
    private static final String COMPLETE_ENDPOINT = "/workflow/complete-task";

    private final ProcessService<InsuranceAggregate> processService;
    private final RestClientService restClientService;
    private final HashMapConverter hashMapConverter;
    private final SortedMapConverter sortedMapConverter;
    private final DataItemConverter dataItemConverter;
    private final InsuranceAggregateRepository insuranceAggregateRepository;
    private final CustomerService customerService;
    private final InsuranceService insuranceService;

    public void startInsuranceWorkflow(TypeDto typeDto) throws Exception {
        InsuranceAggregate insuranceAggregate = new InsuranceAggregate();
        insuranceAggregate.setId(UUID.randomUUID().toString());
        insuranceAggregate.setCreatedAt(LocalDateTime.now());
        insuranceAggregate.setInsuranceType(typeDto.getInsuranceType().toString());
        insuranceAggregate.setCustomerId(typeDto.getCustomerId());
        insuranceAggregate.setFloodRisk(typeDto.isFloodRisk());
        insuranceAggregate.setMudslideRisk(typeDto.isMudslideRisk());
        insuranceAggregate.setSufficientIncome(typeDto.isSufficientIncome());
        processService.startWorkflow(insuranceAggregate);
    }

    @WorkflowTask
    public void checkCreditworthiness(InsuranceAggregate insuranceAggregate) {
        if (insuranceAggregate.isSufficientIncome()) {
            insuranceAggregate.setCreditCheckOutcome("APPROVED");
        } else {
            insuranceAggregate.setCreditCheckOutcome("REJECTED");
        }
        insuranceAggregateRepository.save(insuranceAggregate);
    }

    @WorkflowTask
    public void manualCreditworthinessCheck(InsuranceAggregate insuranceAggregate, @TaskId String taskId, @TaskEvent TaskEvent.Event taskEvent) {

        DataItemGroup customer = addCustomerToAdditionalInfo(insuranceAggregate.getCustomerId());

        List<DataItemGroup> additionalInfoList = new ArrayList<>();
        additionalInfoList.add(customer);

        DataItemGroup account = new DataItemGroup("account");
        if (insuranceAggregate.isSufficientIncome()) {
            account.add(new DataItem<>("monthly", "> € 5000.-"));
        } else {
            account.add(new DataItem<>("monthly", "< € 5000.-"));
        }
        additionalInfoList.add(account);

        TaskDto taskDto = fillTask(taskId,
                                   "Credit Worthiness Check",
                                   "this is for checking manually the credit worthiness",
                                   MODULE_ID,
                                   MODULE_URL,
                                   insuranceAggregate.getId(),
                                   COMPLETE_ENDPOINT);
        taskDto.setAdditionalInfo(dataItemConverter.convertToDatabaseColumn(additionalInfoList));
        log.info(taskEvent.name());
        log.info(taskEvent.toString());
        log.info("task ID: {}", taskId);
        taskDto.setStatus(taskEvent.name());
        if (taskEvent.name().equals(TaskEvent.Event.CANCELED.name())) {
            canceledTask(taskId);
            return;
        }
        restClientService.sendTaskToList(taskDto);
    }

    private TaskDto fillTask(String taskId, String title, String description, String moduleId, String moduleUrl, String aggregateId, String completeEndpoint) {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(taskId);
        taskDto.setTitle(title);
        taskDto.setDescription(description);
        taskDto.setModuleId(moduleId);
        taskDto.setUrl(moduleUrl);
        taskDto.setAggregateId(aggregateId);
        taskDto.setCompleteEndpoint(completeEndpoint);
        return taskDto;
    }

    @WorkflowTask
    public void riskAssessment(InsuranceAggregate insuranceAggregate) {
        if (insuranceAggregate.isFloodRisk() || insuranceAggregate.isMudslideRisk()) {
            insuranceAggregate.setRiskAssessmentOutcome("REJECTED");
        } else {
            insuranceAggregate.setRiskAssessmentOutcome("APPROVED");
        }
    }

    @WorkflowTask
    public void manualRiskAssessment(InsuranceAggregate insuranceAggregate, @TaskId String taskId, @TaskEvent TaskEvent.Event taskEvent) {

        DataItemGroup customer = addCustomerToAdditionalInfo(insuranceAggregate.getCustomerId());
        List<DataItemGroup> additionalInfoList = new ArrayList<>();
        additionalInfoList.add(customer);

        DataItemGroup riskAssessment = new DataItemGroup("riskAssessment");
        if (insuranceAggregate.isMudslideRisk()) {
            riskAssessment.add(new DataItem<>("Vermurungsgefahr", "Ja"));
        } else {
            riskAssessment.add(new DataItem<>("Vermurungsgefahr", "Nein"));
        }
        if (insuranceAggregate.isFloodRisk()) {
            riskAssessment.add(new DataItem<>("Überflutungsgefahr", "Ja"));
        } else {
            riskAssessment.add(new DataItem<>("Überflutungsgefahr", "Nein"));
        }
        additionalInfoList.add(riskAssessment);

        TaskDto taskDto = fillTask(taskId,
                                   "Risk Assessment",
                                   "risk assessment manually",
                                   MODULE_ID,
                                   MODULE_URL,
                                   insuranceAggregate.getId(),
                                   COMPLETE_ENDPOINT);

        taskDto.setAdditionalInfo(dataItemConverter.convertToDatabaseColumn(additionalInfoList));
        log.info(taskEvent.name());
        log.info(taskEvent.toString());
        taskDto.setStatus(taskEvent.name());
        if (taskEvent.name().equals(TaskEvent.Event.CANCELED.name())) {
            canceledTask(taskId);
            return;
        }
        restClientService.sendTaskToList(taskDto);
    }

    @WorkflowTask
    public void generateDocuments(InsuranceAggregate insuranceAggregate) {
        insuranceService.saveInsurance(insuranceAggregate.getCustomerId(), insuranceAggregate.getInsuranceType());
    }

    public void completeUserTask(CompleteTaskDto completeTaskDto) {
        InsuranceAggregate insuranceAggregate;
        if (completeTaskDto.getManualCreditCheckOutcome() != null) {
            insuranceAggregate = setManuallyOutcomes(completeTaskDto.getAggregateId(),
                                                     completeTaskDto.getManualCreditCheckOutcome(),
                                                     null);
        } else if (completeTaskDto.getManualRiskAssessmentOutcome() != null) {
            insuranceAggregate = setManuallyOutcomes(completeTaskDto.getAggregateId(),
                                                     null,
                                                     completeTaskDto.getManualRiskAssessmentOutcome());
        } else {
            insuranceAggregate = insuranceAggregateRepository.findById(completeTaskDto.getAggregateId())
                                                             .orElseThrow(null);
        }
        try {
            processService.completeUserTask(insuranceAggregate, completeTaskDto.getTaskId());
            CompleteTaskEvent completeTaskEvent = new CompleteTaskEvent();
            completeTaskEvent.setTaskId(completeTaskDto.getTaskId());
            completeTaskEvent.setStatus("COMPLETED");
            restClientService.informTaskListAboutCompletedUserTask(completeTaskEvent);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private InsuranceAggregate setManuallyOutcomes(String aggregateId, String manualCreditCheckOutcome, String manualRiskAssessmentOutcome) {
        final InsuranceAggregate insuranceAggregate = insuranceAggregateRepository.findById(aggregateId)
                                                                                  .orElseThrow(null);
        if (manualCreditCheckOutcome != null) {
            insuranceAggregate.setManualCreditCheckOutcome(manualCreditCheckOutcome);
        } else if (manualRiskAssessmentOutcome != null) {
            insuranceAggregate.setManualRiskAssessmentOutcome(manualRiskAssessmentOutcome);
        }
        return insuranceAggregateRepository.save(insuranceAggregate);
    }

    private DataItemGroup addCustomerToAdditionalInfo(UUID customerId) {
        final CustomerDto customerDto = customerService.getCustomer(customerId);
        DataItemGroup customer = new DataItemGroup("customer");
        customer.add(new DataItem<>("name", customerDto.getFirstname() + " " + customerDto.getLastname()))
                .add(new DataItem<>("email", customerDto.getEmail()))
                .add(new DataItem<>("telephone", customerDto.getPhoneNumber()))
                .add(new DataItem<>("birthday", customerDto.getDateOfBirth().toString()))
                .add(new DataItem<>("gender", customerDto.getGender()))
                .add(new DataItem<>("street", customerDto.getStreet()))
                .add(new DataItem<>("zipCode", customerDto.getZipCode()))
                .add(new DataItem<>("city", customerDto.getCity()))
                .add(new DataItem<>("country", customerDto.getCountry()));
        return customer;
    }

    private void canceledTask(String taskId) {
        CompleteTaskEvent completeTaskEvent = new CompleteTaskEvent();
        completeTaskEvent.setTaskId(taskId);
        completeTaskEvent.setStatus(TaskEvent.Event.CANCELED.name());
        restClientService.informTaskListAboutCompletedUserTask(completeTaskEvent);
    }

}
