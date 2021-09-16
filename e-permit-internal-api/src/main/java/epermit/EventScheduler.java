package epermit;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import epermit.appevents.LedgerEventCreated;
import epermit.appevents.LedgerEventReplay;
import epermit.ledgerevents.LedgerEventResult;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 3 * 60 * 1000)
    @SneakyThrows
    public void unsentEventsTask() {
        log.info("Unsent events task started");
        List<LedgerEventReplay> list = eventService.getUnSendedEvents();
        for (LedgerEventReplay ledgerEventReplay : list) {
            Thread.sleep(1 * 1000);
            eventPublisher.publishEvent(ledgerEventReplay);
        }
    }
}
