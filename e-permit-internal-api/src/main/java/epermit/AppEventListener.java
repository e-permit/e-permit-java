package epermit;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import epermit.appevents.LedgerEventCreated;
import epermit.ledgerevents.LedgerEventUtil;
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
        log.info("onAppEvent is fired. {}", event);
        Boolean isOk = ledgerEventUtil.sendEvent(event);
        if (isOk) {
            eventService.handleSendedEvent(event.getEventId());
        }
        log.info("Sending event is finished");
    }
}
