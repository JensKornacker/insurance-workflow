package at.phactum.demo.workflow.service;

import at.phactum.demo.workflow.dto.CompleteTaskEvent;
import at.phactum.demo.workflow.dto.TaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class RestClientService {

    @Value("${tasklist.base-url}")
    private String taskListUrl;
    private final RestClient restClient;

    public RestClientService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public void sendTaskToList(TaskDto taskDto) {
        ResponseEntity<Void> response = restClient.post()
                                                  .uri(taskListUrl + "/tasks")
                                                  .body(taskDto)
                                                  .retrieve()
                                                  .toBodilessEntity();
        log.info(response.toString());
    }

    public void informTaskListAboutCompletedUserTask(CompleteTaskEvent completeTaskEvent) {
        ResponseEntity<Void> response = restClient.post()
                                                  .uri(taskListUrl + "/tasks/complete")
                                                  .body(completeTaskEvent)
                                                  .retrieve()
                                                  .toBodilessEntity();
        log.info(response.toString());
    }

}
