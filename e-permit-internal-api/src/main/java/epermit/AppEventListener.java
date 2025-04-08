package epermit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

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

    @Async
    @TransactionalEventListener(fallbackExecution = true)
    public void onAppEvent(LedgerEventCreated event) {
        log.info("OnAppEvent is fired. {}", event);
        try {
            eventService.sendEvent(event);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("Sending event is finished");
    }
}
