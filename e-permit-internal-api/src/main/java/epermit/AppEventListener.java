package epermit;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import epermit.appevents.LedgerEventCreated;
import epermit.ledgerevents.LedgerEventResult;
import epermit.models.enums.AuthenticationType;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
@Component
public class AppEventListener {
    private final RestTemplate restTemplate;
    private final EventService eventService;

    @Async
    @TransactionalEventListener
    public void onAppEvent(LedgerEventCreated event) {
        log.info("onAppEvent is fired. {}", event);
        sendEvent(event);
        log.info("Sending event is finished");
    }

    @SneakyThrows
    private void sendEvent(LedgerEventCreated event) {
        HttpHeaders headers = createEventRequestHeader(event.getProofType(), event.getProof());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(event.getContent(), headers);
        LedgerEventResult result =
                restTemplate.postForObject(event.getUri(), request, LedgerEventResult.class);
        if (result.isOk()) {
            eventService.handleSendedEvent(event.getEventId());
        }
    }

    private HttpHeaders createEventRequestHeader(AuthenticationType proofType, String proof) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization",
                proofType == AuthenticationType.BASIC ? "Basic " : "Bearer " + proof);
        return headers;
    }
}
