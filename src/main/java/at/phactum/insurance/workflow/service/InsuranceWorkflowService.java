package at.phactum.insurance.workflow.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import at.phactum.insurance.dto.CompleteTaskDto;
import at.phactum.insurance.dto.CompleteTaskEvent;
import at.phactum.insurance.dto.TaskDto;
import at.phactum.insurance.dto.TypeDto;
import at.phactum.insurance.utils.HashMapConverter;
import at.phactum.insurance.workflow.persistence.InsuranceAggregate;
import at.phactum.insurance.workflow.persistence.InsuranceAggregateRepository;
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

    public void startInsuranceWorkflow(TypeDto typeDto) throws Exception {
        InsuranceAggregate insuranceAggregate = new InsuranceAggregate();
        insuranceAggregate.setId(UUID.randomUUID().toString());
        insuranceAggregate.setCreatedAt(LocalDateTime.now());
        processService.startWorkflow(insuranceAggregate);
    }

    @WorkflowTask
    public void checkCreditworthiness(InsuranceAggregate insuranceAggregate) {
        // this is empty for reasons
    }

    @WorkflowTask
    public void manualCreditworthinessCheck(InsuranceAggregate insuranceAggregate, @TaskId String taskId) {
        log.info("task ID: {}", taskId);
        Map<String, Object> addInfo = new HashMap<>();
        addInfo.put("worthiness", "check worthiness");
        addInfo.put("account", "check coverage");
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(taskId);
        taskDto.setTitle("Manual Worthiness Check");
        taskDto.setDescription("this is for checking manually the creditworthiness");
        taskDto.setModuleId("insurance");
        taskDto.setUrl("http://localhost:8080");
        taskDto.setAggregateId(insuranceAggregate.getId());
        taskDto.setCompleteEndpoint("/insurance/complete-task");
        taskDto.setAdditionalInfo(hashMapConverter.convertToDatabaseColumn(addInfo));
        restClientService.sendTaskToList(taskDto);
    }

    @WorkflowTask
    public void riskAssessment(InsuranceAggregate insuranceAggregate) {
        insuranceAggregate.setRiskAssessmentOutcome("APPROVED");
    }

    @WorkflowTask
    public void manualRiskAssessment(InsuranceAggregate insuranceAggregate, @TaskId String taskId) {
        TaskDto taskDto = new TaskDto();

        Map<String, Object> addInfo = new HashMap<>();
        addInfo.put("risk assessment", "schlechtes Wetter");
        String riskAssessment = hashMapConverter.convertToDatabaseColumn(addInfo);
        taskDto.setTaskId(taskId);
        taskDto.setTitle("Manual Risk Assessment");
        taskDto.setDescription("risk assessment manually");
        taskDto.setModuleId("insurance");
        taskDto.setUrl("http://localhost:8080");
        taskDto.setAggregateId(insuranceAggregate.getId());
        taskDto.setCompleteEndpoint("/insurance/complete-task");
        taskDto.setAdditionalInfo(riskAssessment);
        restClientService.sendTaskToList(taskDto);
        log.info("task ID: {}", taskId);
    }

    @WorkflowTask
    public void generateDocuments(InsuranceAggregate insuranceAggregate) {

    }

    public void completeUserTask(CompleteTaskDto completeTaskDto) {
        InsuranceAggregate insuranceAggregate = null;
        if (completeTaskDto.getManualCreditCheckOutcome() != null) {
            insuranceAggregate = setManualCreditCheckOutcome(completeTaskDto.getAggregateId(), completeTaskDto.getManualCreditCheckOutcome());
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

    private InsuranceAggregate setManualCreditCheckOutcome(String aggregateId, String manualCreditCheckOutcome) {
        final InsuranceAggregate insuranceAggregate = insuranceAggregateRepository.findById(aggregateId)
                                                                                  .orElseThrow(null);
        insuranceAggregate.setManualCreditCheckOutcome(manualCreditCheckOutcome);
        return insuranceAggregateRepository.save(insuranceAggregate);
    }

}
