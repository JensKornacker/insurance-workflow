package at.phactum.demo.workflow.dto;

import lombok.Data;

@Data
public class TaskDto {
    private String taskId;
    private String title;
    private String description;
    private String moduleId;
    private String url;
    private String completeEndpoint;
    private String additionalInfo;
    private String aggregateId;
    private String status;
}
