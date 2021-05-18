package epermit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import epermit.events.CreatedAppEvent;

@EnableAsync(proxyTargetClass = true)
@Component
public class AppEventListener {
    private final RestTemplate restTemplate;

    public AppEventListener(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    @TransactionalEventListener
    public void onAppEvent(CreatedAppEvent event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(event.getJws(), headers);
        restTemplate.postForEntity(event.getUri(), request, Boolean.class);
    }
}
