package at.phactum.demo.workflow.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TaskDto {
    String taskId;
    String title;
    String description;
    String moduleId;
    String url;
    String completeEndpoint;
    String additionalInfo;
    String aggregateId;
    String status;
    String config;
    String configData;
    String taskDefinition;
    String type;
}
