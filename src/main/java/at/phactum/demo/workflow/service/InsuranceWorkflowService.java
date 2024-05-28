package at.phactum.demo.workflow.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.service.CustomerService;
import at.phactum.demo.shared.utils.HashMapConverter;
import at.phactum.demo.workflow.dto.CompleteTaskDto;
import at.phactum.demo.workflow.dto.CompleteTaskEvent;
import at.phactum.demo.workflow.dto.TaskDto;
import at.phactum.demo.workflow.dto.TypeDto;
import at.phactum.demo.workflow.persistence.InsuranceAggregate;
import at.phactum.demo.workflow.persistence.InsuranceAggregateRepository;
import io.vanillabp.spi.process.ProcessService;
import io.vanillabp.spi.service.BpmnProcess;
import io.vanillabp.spi.service.TaskId;
import io.vanillabp.spi.service.WorkflowService;
import io.vanillabp.spi.service.WorkflowTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@WorkflowService(workflowAggregateClass = InsuranceAggregate.class,
        bpmnProcess = @BpmnProcess(bpmnProcessId = "Process_InsuranceContractWorkflow"))
@Slf4j
@RequiredArgsConstructor
public class InsuranceWorkflowService {

    private final ProcessService<InsuranceAggregate> processService;
    private final RestClientService restClientService;
    private final HashMapConverter hashMapConverter;
    private final InsuranceAggregateRepository insuranceAggregateRepository;
    private final CustomerService customerService;

    public void startInsuranceWorkflow(TypeDto typeDto) throws Exception {
        InsuranceAggregate insuranceAggregate = new InsuranceAggregate();
        insuranceAggregate.setId(UUID.randomUUID().toString());
        insuranceAggregate.setCreatedAt(LocalDateTime.now());
        insuranceAggregate.setInsuranceType(typeDto.getInsuranceType().toString());
        insuranceAggregate.setCustomer(typeDto.getCustomerId());
        insuranceAggregate.setFloodRisk(typeDto.isFloodRisk());
        insuranceAggregate.setMudslideRisk(typeDto.isMudslideRisk());
        processService.startWorkflow(insuranceAggregate);
    }

    @WorkflowTask
    public void checkCreditworthiness(InsuranceAggregate insuranceAggregate) {
        // this is empty for reasons
    }

    @WorkflowTask
    public void manualCreditworthinessCheck(InsuranceAggregate insuranceAggregate, @TaskId String taskId) {
        log.info("task ID: {}", taskId);
        final CustomerDto customerDto = customerService.getCustomer(insuranceAggregate.getCustomer());
        Map<String, Object> addInfo = new HashMap<>();
        addCustomerToAdditionalInfo(addInfo, customerDto);
        addInfo.put("worthiness", "check worthiness");
        addInfo.put("account", "check coverage");

        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(taskId);
        taskDto.setTitle("Manual Worthiness Check");
        taskDto.setDescription("this is for checking manually the creditworthiness");
        taskDto.setModuleId("insurance");
        taskDto.setUrl("http://localhost:8080");
        taskDto.setAggregateId(insuranceAggregate.getId());
        taskDto.setCompleteEndpoint("/workflow/complete-task");
        taskDto.setAdditionalInfo(hashMapConverter.convertToDatabaseColumn(addInfo));
        restClientService.sendTaskToList(taskDto);
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
    public void manualRiskAssessment(InsuranceAggregate insuranceAggregate, @TaskId String taskId) {
        final CustomerDto customerDto = customerService.getCustomer(insuranceAggregate.getCustomer());
        Map<String, Object> addInfo = new HashMap<>();
        addInfo.put("risk assessment", "schlechtes Wetter");
        addInfo.put("mudslide_risk", insuranceAggregate.isMudslideRisk());
        addInfo.put("flood_risk", insuranceAggregate.isFloodRisk());
        addCustomerToAdditionalInfo(addInfo, customerDto);
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(taskId);
        taskDto.setTitle("Manual Risk Assessment");
        taskDto.setDescription("risk assessment manually");
        taskDto.setModuleId("insurance");
        taskDto.setUrl("http://localhost:8080");
        taskDto.setAggregateId(insuranceAggregate.getId());
        taskDto.setCompleteEndpoint("/workflow/complete-task");
        taskDto.setAdditionalInfo(hashMapConverter.convertToDatabaseColumn(addInfo));
        restClientService.sendTaskToList(taskDto);
    }

    @WorkflowTask
    public void generateDocuments(InsuranceAggregate insuranceAggregate) {

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

    private void addCustomerToAdditionalInfo(Map<String, Object> addInfo, CustomerDto customerDto) {
        addInfo.put("customer", customerDto.getFirstname() + " " + customerDto.getLastname());
        addInfo.put("customerEmail", customerDto.getEmail());
        addInfo.put("customerPhoneNumber", customerDto.getPhoneNumber());
        addInfo.put("customerDateOfBirth", customerDto.getDateOfBirth().toString());
        addInfo.put("customerGender", customerDto.getGender().toString());
    }

}
