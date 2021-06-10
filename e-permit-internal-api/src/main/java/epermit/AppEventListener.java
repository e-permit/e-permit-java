package epermit;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import epermit.events.CreatedAppEvent;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
@Component
public class AppEventListener {
    private final RestTemplate restTemplate;
    private final JwsUtil jwsUtil;

    @Async
    @TransactionalEventListener
    public void onAppEvent(CreatedAppEvent event) {
        log.info("onAppEvent is fired. {}", event);
        HttpHeaders headers = jwsUtil.getJwsHeader(event.getJws());
        HttpEntity<String> request = new HttpEntity<String>(event.getJws(), headers);
        restTemplate.postForEntity(event.getUri(), request, Boolean.class);
    }
}
