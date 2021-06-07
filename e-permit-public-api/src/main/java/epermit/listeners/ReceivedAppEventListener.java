package epermit.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import epermit.events.ReceivedAppEvent;
import epermit.services.ReceivedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
public class ReceivedAppEventListener {
    private final ReceivedEventService eventService;

    @Async
    @EventListener
    public void onAppEvent(ReceivedAppEvent event) {
        log.info("Event received. {}", event);
        eventService.handleReceivedEvent(event);
    }
}
