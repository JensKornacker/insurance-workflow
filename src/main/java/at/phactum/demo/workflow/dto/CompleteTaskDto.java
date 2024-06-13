package at.phactum.demo.workflow.dto;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteTaskDto {
    String taskId;
    String aggregateId;
    String taskDefinition;
    Map<String, Object> completeVars;
    String manualCreditCheckOutcome;
    String manualRiskAssessmentOutcome;
}
