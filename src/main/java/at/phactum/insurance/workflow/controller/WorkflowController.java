package at.phactum.insurance.workflow.controller;

import at.phactum.insurance.dto.TypeDto;
import at.phactum.insurance.workflow.service.InsuranceWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("insurance")
@RequiredArgsConstructor
public class WorkflowController {

    private final InsuranceWorkflowService insuranceWorkflowService;

    @PostMapping
    public ResponseEntity<Void> requestInsurance(@RequestBody TypeDto typeDto) throws Exception {
        insuranceWorkflowService.startInsuranceWorkflow(typeDto);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<String> getRequest() {
        return ResponseEntity.ok().body("Get Request");
    }

}
