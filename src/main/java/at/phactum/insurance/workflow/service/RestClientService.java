package at.phactum.insurance.workflow.service;

import at.phactum.insurance.dto.CompleteTaskDto;
import at.phactum.insurance.dto.CompleteTaskEvent;
import at.phactum.insurance.dto.TaskDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class RestClientService {

    private final RestClient restClient;

    public RestClientService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:1111").build();
    }

    public void sendTaskToList(TaskDto taskDto) {
        ResponseEntity<Void> response = restClient.post()
                                                  .uri("/tasks")
                                                  .body(taskDto)
                                                  .retrieve()
                                                  .toBodilessEntity();
        log.info(response.toString());
    }

    public void informTaskListAboutCompletedUserTask(CompleteTaskEvent completeTaskEvent) {
        ResponseEntity<Void> response = restClient.post()
                                                  .uri("/tasks/complete")
                                                  .body(completeTaskEvent)
                                                  .retrieve()
                                                  .toBodilessEntity();
        log.info(response.toString());
    }

}
