package epermit.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import epermit.appevents.QuotaCreated;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class QuotaCreatedEventListener {
    private final AuthorityService authorityService;
    @Async
    @EventListener
    public void onAppEvent(QuotaCreated event) {
        log.info("onAppEvent is fired. {}", event);
        authorityService.handleReceivedQuota(event);
        log.info("Sending event is finished");
    }
}
