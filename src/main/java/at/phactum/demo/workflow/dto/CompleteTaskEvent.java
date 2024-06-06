package at.phactum.demo.workflow.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompleteTaskEvent {
    String taskId;
    String status;
}
