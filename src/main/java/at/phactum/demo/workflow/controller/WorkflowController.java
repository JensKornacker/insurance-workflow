package at.phactum.demo.workflow.controller;

import at.phactum.demo.workflow.dto.CompleteTaskDto;
import at.phactum.demo.workflow.dto.TypeDto;
import at.phactum.demo.workflow.service.InsuranceWorkflowService;
import at.phactum.demo.workflow.service.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("workflow")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class WorkflowController {

    private final InsuranceWorkflowService insuranceWorkflowService;

    @PostMapping
    public ResponseEntity<Message> requestInsurance(@RequestBody TypeDto typeDto) throws Exception {
        insuranceWorkflowService.startInsuranceWorkflow(typeDto);
        return ResponseEntity.accepted().body(new Message("Insurance successfully requested"));
    }

    @PostMapping("complete-task")
    public ResponseEntity<Void> completeTask(@RequestBody CompleteTaskDto completeTaskDto) {
        insuranceWorkflowService.completeUserTask(completeTaskDto);
        return ResponseEntity.ok()
                             .build();
    }

}
