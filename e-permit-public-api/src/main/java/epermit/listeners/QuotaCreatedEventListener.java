package epermit.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
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
    @TransactionalEventListener
    public void onAppEvent(QuotaCreated event) {
        log.info("onAppEvent is fired. {}", event);
        authorityService.handleReceivedQuota(event);
        log.info("Sending event is finished");
    }
}
