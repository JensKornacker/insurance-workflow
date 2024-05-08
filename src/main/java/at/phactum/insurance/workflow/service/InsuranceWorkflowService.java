package at.phactum.insurance.workflow.service;

import at.phactum.insurance.dto.TypeDto;
import at.phactum.insurance.workflow.persistence.InsuranceAggregate;
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

    public void startInsuranceWorkflow(TypeDto typeDto) throws Exception {
        InsuranceAggregate insuranceAggregate = new InsuranceAggregate();
        processService.startWorkflow(insuranceAggregate);
    }

    @WorkflowTask
    public void checkCreditworthiness(InsuranceAggregate insuranceAggregate) {
        // this is empty for reasons
    }

    @WorkflowTask
    public void manualCreditworthinessCheck(InsuranceAggregate insuranceAggregate, @TaskId String taskId) {
        log.info("task ID: {}", taskId);
    }

    @WorkflowTask
    public void riskAssessment() {

    }

    @WorkflowTask
    public void manualRiskAssessment(InsuranceAggregate insuranceAggregate, @TaskId String taskId) {
        log.info("task ID: {}", taskId);
    }

    @WorkflowTask
    public void generateDocuments(InsuranceAggregate insuranceAggregate) {

    }

}
