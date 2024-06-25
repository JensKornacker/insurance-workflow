package at.phactum.demo.workflow.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import at.phactum.demo.customer.dto.CustomerDto;
import at.phactum.demo.customer.service.CustomerService;
import at.phactum.demo.insurance.service.InsuranceService;
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

    private static final String POSITION = "position";
    private static final String MONTHLY_INCOME = "monthlyIncome";
    private static final String MANUAL_CREDIT_CHECK_OUTCOME = "manualCreditCheckOutcome";
    private static final String MANUAL_RISK_ASSESSMENT_OUTCOME = "manualRiskAssessmentOutcome";
    private static final String TASK_DEFINITION_WORTHINESS_CHECK = "manualCreditworthinessCheck";
    private static final String TASK_DEFINITION_RISK_ASSESSMENT = "manualRiskAssessment";
    private static final String TASK_DEFINITION_LIABILITY_CHECK = "manualLiabilityCheck";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final String[] APPROVAL_ARRAY = new String[]{APPROVED, REJECTED};

    private static final String MODULE_ID = "insurance";
    private static final String MODULE_URL = "http://localhost:8080";
    private static final String COMPLETE_ENDPOINT = "/workflow/complete-task";

    private final ProcessService<InsuranceAggregate> processService;
    private final RestClientService restClientService;
    private final HashMapConverter hashMapConverter;
    private final SortedMapConverter sortedMapConverter;
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
        insuranceAggregate.setInsuranceSum(typeDto.getInsuranceSum());
        insuranceAggregate.setInsuranceCoverage(typeDto.getInsuranceCoverage());
        insuranceAggregate.setPaymentSchedule(typeDto.getPaymentSchedule());
        insuranceAggregate.setAmount(typeDto.getAmount());
        processService.startWorkflow(insuranceAggregate);
    }

    @WorkflowTask
    public void checkCreditworthiness(InsuranceAggregate insuranceAggregate) {
        if (insuranceAggregate.isSufficientIncome()) {
            insuranceAggregate.setCreditCheckOutcome(APPROVED);
        } else {
            insuranceAggregate.setCreditCheckOutcome(REJECTED);
        }
        insuranceAggregateRepository.save(insuranceAggregate);
    }

    @WorkflowTask
    public void manualCreditworthinessCheck(InsuranceAggregate insuranceAggregate, @TaskId String taskId, @TaskEvent TaskEvent.Event taskEvent) {

        SortedMap<String, SortedMap<String, String>> addInfo = getCustomerToAdditionalInfoMap(insuranceAggregate.getCustomerId());

        SortedMap<String, String> account = new TreeMap<>();
        if (insuranceAggregate.isSufficientIncome()) {
            account.put("monatliches Gehalt", "> € 5000.-");
        } else {
            account.put("monatliches Gehalt", "< € 5000.-");
        }
        addInfo.put("Konto", account);

        Map<String, Object> config = new HashMap<>();

        config.put(MONTHLY_INCOME, "");
        config.put(MANUAL_CREDIT_CHECK_OUTCOME, "??");

        Map<String, Object> configData = new HashMap<>();
        Map<String, Object> worthyMap = new HashMap<>();
        worthyMap.put(POSITION, 1);
        worthyMap.put("type", "enum");
        worthyMap.put("values", APPROVAL_ARRAY);
        configData.put(MANUAL_CREDIT_CHECK_OUTCOME, worthyMap);

        Map<String, Object> monthlyIncomeMap = new HashMap<>();
        monthlyIncomeMap.put(POSITION, 2);
        monthlyIncomeMap.put("type", "number");

        configData.put(MONTHLY_INCOME, monthlyIncomeMap);
        TaskDto taskDto = new TaskDto(taskId,
                                      "Credit Worthiness Check",
                                      "this is for checking manually the credit worthiness",
                                      MODULE_ID,
                                      MODULE_URL,
                                      COMPLETE_ENDPOINT,
                                      sortedMapConverter.convertToDatabaseColumn(addInfo),
                                      insuranceAggregate.getId(),
                                      taskEvent.name(),
                                      hashMapConverter.convertToDatabaseColumn(config),
                                      hashMapConverter.convertToDatabaseColumn(configData),
                                      TASK_DEFINITION_WORTHINESS_CHECK, insuranceAggregate.getInsuranceType());

        log.info(taskEvent.name());
        log.info(taskEvent.toString());
        log.info("task ID: {}", taskId);

        if (taskEvent.name().equals(TaskEvent.Event.CANCELED.name())) {
            canceledTask(taskId);
            return;
        }
        restClientService.sendTaskToList(taskDto);
    }

    @WorkflowTask
    public void riskAssessment(InsuranceAggregate insuranceAggregate) {
        if (insuranceAggregate.isFloodRisk() || insuranceAggregate.isMudslideRisk()) {
            insuranceAggregate.setRiskAssessmentOutcome(REJECTED);
        } else {
            insuranceAggregate.setRiskAssessmentOutcome(APPROVED);
        }
    }

    @WorkflowTask
    public void manualRiskAssessment(InsuranceAggregate insuranceAggregate, @TaskId String taskId, @TaskEvent TaskEvent.Event taskEvent) {

        SortedMap<String, SortedMap<String, String>> addInfo = getCustomerToAdditionalInfoMap(insuranceAggregate.getCustomerId());

        SortedMap<String, String> riskAssessment = new TreeMap<>();
        if (insuranceAggregate.isMudslideRisk()) {
            riskAssessment.put("Vermurungsgefahr", "Ja");
        } else {
            riskAssessment.put("Vermurungsgefahr", "Nein");
        }
        if (insuranceAggregate.isFloodRisk()) {
            riskAssessment.put("Überflutungsgefahr", "Ja");
        } else {
            riskAssessment.put("Überflutungsgefahr", "Nein");
        }
        addInfo.put("Risikoabschätzung", riskAssessment);

        Map<String, Object> config = new HashMap<>();

        config.put("furtherInformation", "");
        config.put(MANUAL_RISK_ASSESSMENT_OUTCOME, "??");

        Map<String, Object> configData = new HashMap<>();
        Map<String, Object> riskyMap = new HashMap<>();
        riskyMap.put(POSITION, 1);
        riskyMap.put("type", "enum");
        riskyMap.put("values", APPROVAL_ARRAY);
        configData.put(MANUAL_RISK_ASSESSMENT_OUTCOME, riskyMap);

        Map<String, Object> riskDescription = new HashMap<>();
        riskDescription.put(POSITION, 2);
        riskDescription.put("type", "textarea");

        configData.put("furtherInformation", riskDescription);

        TaskDto taskDto = new TaskDto(taskId,
                                      "Risk Assessment",
                                      "risk assessment manually",
                                      MODULE_ID,
                                      MODULE_URL,
                                      COMPLETE_ENDPOINT,
                                      sortedMapConverter.convertToDatabaseColumn(addInfo),
                                      insuranceAggregate.getId(),
                                      taskEvent.name(),
                                      hashMapConverter.convertToDatabaseColumn(config),
                                      hashMapConverter.convertToDatabaseColumn(configData),
                                      TASK_DEFINITION_RISK_ASSESSMENT, insuranceAggregate.getInsuranceType());

        log.info(taskEvent.name());
        log.info(taskEvent.toString());
        if (taskEvent.name().equals(TaskEvent.Event.CANCELED.name())) {
            canceledTask(taskId);
            return;
        }
        restClientService.sendTaskToList(taskDto);
    }

    @WorkflowTask
    public void manualLiabilityCheck(InsuranceAggregate insuranceAggregate, @TaskId String taskId, @TaskEvent TaskEvent.Event taskEvent) {
        if (taskEvent.name().equals(TaskEvent.Event.CANCELED.name())) {
            canceledTask(taskId);
            return;
        }
        SortedMap<String, SortedMap<String, String>> addInfo = getCustomerToAdditionalInfoMap(insuranceAggregate.getCustomerId());
        SortedMap<String, String> liabilityCheck = new TreeMap<>();
        liabilityCheck.put("insuranceCoverage", insuranceAggregate.getInsuranceCoverage());
        liabilityCheck.put("insuranceSum", insuranceAggregate.getInsuranceSum());
        addInfo.put("liabilityCheck", liabilityCheck);

        Map<String, Object> config = new HashMap<>();
        config.put("furtherInformation", "??");
        config.put("approveProposal", "??");
        Map<String, Object> furtherChecks = setTaskConfigData(1, "textarea", null);
        Map<String, Object> approveProposal = setTaskConfigData(2, "enum", APPROVAL_ARRAY);
        Map<String, Object> configData = new HashMap<>();
        configData.put("furtherInformation", furtherChecks);
        configData.put("approveProposal", approveProposal);

        TaskDto taskDto = new TaskDto(taskId,
                                      "Liability Check",
                                      "Checking manually some background information for Liability Insurance like how many kids and things like this",
                                      MODULE_ID,
                                      MODULE_URL,
                                      COMPLETE_ENDPOINT,
                                      sortedMapConverter.convertToDatabaseColumn(addInfo),
                                      insuranceAggregate.getId(),
                                      taskEvent.name(),
                                      hashMapConverter.convertToDatabaseColumn(config),
                                      hashMapConverter.convertToDatabaseColumn(configData),
                                      TASK_DEFINITION_LIABILITY_CHECK, insuranceAggregate.getInsuranceType());
        log.info(taskEvent.name());
        log.info(taskEvent.toString());

        restClientService.sendTaskToList(taskDto);
    }

    private Map<String, Object> setTaskConfigData(int position, String type, String[] values) {
        Map<String, Object> firstMap = new HashMap<>();
        firstMap.put(POSITION, position);
        firstMap.put("type", type);
        firstMap.put("values", values);
        return firstMap;
    }

    @WorkflowTask
    public void generateDocuments(InsuranceAggregate insuranceAggregate) {
        insuranceService.saveInsurance(insuranceAggregate.getCustomerId(),
                                       insuranceAggregate.getInsuranceType(),
                                       insuranceAggregate.getInsuranceCoverage(),
                                       insuranceAggregate.getInsuranceSum(),
                                       insuranceAggregate.getPaymentSchedule(),
                                       insuranceAggregate.getAmount());
    }

    public void completeUserTask(CompleteTaskDto completeTaskDto) {
        InsuranceAggregate insuranceAggregate = insuranceAggregateRepository.findById(completeTaskDto.getAggregateId())
                                                                            .orElseThrow(null);
        switch (completeTaskDto.getTaskDefinition()) {
            case TASK_DEFINITION_WORTHINESS_CHECK -> {
                for (Map.Entry<String, Object> entry : completeTaskDto.getCompleteVars().entrySet()) {
                    if (entry.getKey().equals(MANUAL_CREDIT_CHECK_OUTCOME)) {
                        insuranceAggregate.setManualCreditCheckOutcome(entry.getValue().toString());
                    }
                    if (entry.getKey().equals(MONTHLY_INCOME)) {
                        insuranceAggregate.setMonthlyIncome(Integer.valueOf(entry.getValue().toString()));
                    }
                }
            }
            case TASK_DEFINITION_RISK_ASSESSMENT -> {
                for (Map.Entry<String, Object> entry : completeTaskDto.getCompleteVars().entrySet()) {
                    if (entry.getKey().equals(MANUAL_RISK_ASSESSMENT_OUTCOME)) {
                        insuranceAggregate.setManualRiskAssessmentOutcome(entry.getValue().toString());
                    }
                    if (entry.getKey().equals("furtherInformation")) {
                        insuranceAggregate.setFurtherInformation(entry.getValue().toString());
                    }
                }
            }
            case TASK_DEFINITION_LIABILITY_CHECK -> {
                for (Map.Entry<String, Object> entry : completeTaskDto.getCompleteVars().entrySet()) {
                    if (entry.getKey().equals("approveProposal")) {
                        insuranceAggregate.setLiabilityCheck(entry.getValue().toString());
                    }
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + completeTaskDto.getTaskDefinition());
        }
        insuranceAggregate.setUpdatedAt(LocalDateTime.now());
        final InsuranceAggregate saved = insuranceAggregateRepository.save(insuranceAggregate);

        try {
            processService.completeUserTask(saved, completeTaskDto.getTaskId());
            CompleteTaskEvent completeTaskEvent = new CompleteTaskEvent();
            completeTaskEvent.setTaskId(completeTaskDto.getTaskId());
            completeTaskEvent.setStatus("COMPLETED");
            restClientService.informTaskListAboutCompletedUserTask(completeTaskEvent);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private SortedMap<String, SortedMap<String, String>> getCustomerToAdditionalInfoMap(UUID customerId) {
        final CustomerDto customerDto = customerService.getCustomer(customerId);
        SortedMap<String, SortedMap<String, String>> addInfo = new TreeMap<>();
        return addCustomerToAdditionalInfo(addInfo, customerDto);
    }

    private SortedMap<String, SortedMap<String, String>> addCustomerToAdditionalInfo(SortedMap<String, SortedMap<String, String>> addInfo, CustomerDto customerDto) {

        SortedMap<String, String> customerMap = new TreeMap<>();
        customerMap.put("Name", customerDto.getFirstname() + " " + customerDto.getLastname());
        customerMap.put("Email", customerDto.getEmail());
        customerMap.put("Telephon", customerDto.getPhoneNumber());
        customerMap.put("Geburtstag", customerDto.getDateOfBirth().toString());
        customerMap.put("Geschlecht", customerDto.getGender().toString());
        addInfo.put("customer", customerMap);
        return addInfo;
    }

    private void canceledTask(String taskId) {
        CompleteTaskEvent completeTaskEvent = new CompleteTaskEvent();
        completeTaskEvent.setTaskId(taskId);
        completeTaskEvent.setStatus(TaskEvent.Event.CANCELED.name());
        restClientService.informTaskListAboutCompletedUserTask(completeTaskEvent);
    }

}
