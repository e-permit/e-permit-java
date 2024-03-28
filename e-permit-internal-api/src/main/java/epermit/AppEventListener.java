package epermit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import epermit.commons.ApiErrorResponse;
import epermit.commons.GsonUtil;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.LedgerEventCreated;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
@Component
public class AppEventListener {
    private final EventService eventService;
    private final LedgerEventUtil ledgerEventUtil;

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void onAppEvent(LedgerEventCreated event) {
        log.info("OnAppEvent is fired. {}", event);
        ResponseEntity<?> result = ledgerEventUtil.sendEvent(event);
        if (result.getStatusCode() == HttpStatus.OK) {
            eventService.handleSentEvent(event.getEventId());
        } else if (result.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
            ApiErrorResponse error = (ApiErrorResponse) result.getBody();
            if (error != null && error.getDetails().get("errorCode").equals("EVENT_ALREADY_EXISTS")) {
                eventService.handleSentEvent(event.getEventId());
            } else {
                String err = GsonUtil.getGson().toJson(result.getBody());
                log.error(err);
                eventService.handleEventError(event.getEventId(), err);
            }
        }
        log.info("Sending event is finished");
    }
}
