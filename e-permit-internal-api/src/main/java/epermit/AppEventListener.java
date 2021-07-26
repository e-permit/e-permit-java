package epermit;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import epermit.commons.ErrorCodes;
import epermit.ledgerevents.LedgerEventCreated;
import epermit.ledgerevents.LedgerEventResult;
import epermit.ledgerevents.LedgerEventUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
@Component
public class AppEventListener {
    private final RestTemplate restTemplate;
    private final LedgerEventUtil ledgerEventUtil;

    @Async
    @TransactionalEventListener

    public void onAppEvent(LedgerEventCreated event) {
        log.info("onAppEvent is fired. {}", event);
        sendEvent(event);
        log.info("Sending event is finished");
    }

    @SneakyThrows
    private void sendEvent(LedgerEventCreated event) {
        HttpHeaders headers =
                ledgerEventUtil.createEventRequestHeader(event.getProofType(), event.getProof());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(event.getContent(), headers);
        LedgerEventResult result =
                restTemplate.postForObject(event.getUri(), request, LedgerEventResult.class);
        if (!result.isOk()) {
            if (result.getErrorCode() == ErrorCodes.PREVIOUS_EVENT_NOTFOUND.name()) {
                Thread.sleep(Long.valueOf(1 * 60 * 1000));
                sendEvent(event);
            }
        }
    }
}
