package epermit.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import epermit.ledgerevents.LedgerEventReceived;
import epermit.services.PersistedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
public class LedgerEventReceivedListener {
    private final PersistedEventService  eventService;

    @Async
    @EventListener
    public void onAppEvent(LedgerEventReceived event) {
        log.info("Event received. {}", event);
        eventService.handleLedgerEventReceived(event);
    }
}
