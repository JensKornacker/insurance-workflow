package at.phactum.demo.workflow.controller;

import java.util.Map;

import at.phactum.demo.shared.utils.HashMapConverter;
import at.phactum.demo.workflow.dto.CompleteTaskDto;
import at.phactum.demo.workflow.dto.TypeDto;
import at.phactum.demo.workflow.service.InsuranceWorkflowService;
import at.phactum.demo.workflow.service.Message;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class WorkflowController {

    private final InsuranceWorkflowService insuranceWorkflowService;
    private final HashMapConverter hashMapConverter;

    @PostMapping
    public ResponseEntity<Message> requestInsurance(@RequestBody TypeDto typeDto) throws Exception {
        insuranceWorkflowService.startInsuranceWorkflow(typeDto);
        return ResponseEntity.accepted().body(new Message("Insurance successfully requested"));
    }

    @PostMapping("complete-task")
    public ResponseEntity<Void> completeTask(@RequestBody CompleteTaskDto completeTaskDto) {
        insuranceWorkflowService.completeUserTask(completeTaskDto);
        for (Map.Entry<String, Object> entry : completeTaskDto.getCompleteVars().entrySet()) {
            log.info(entry.getKey() + ":" + entry.getValue());
        }
        return ResponseEntity.ok()
                             .build();
    }

}
