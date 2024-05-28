package at.phactum.demo.workflow.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteTaskDto {
    String taskId;
    String aggregateId;
    String manualCreditCheckOutcome;
    String manualRiskAssessmentOutcome;
}
