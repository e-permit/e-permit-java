package epermit;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import epermit.appevents.LedgerEventCreated;
import epermit.ledgerevents.LedgerEventResult;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;
    private final LedgerEventUtil ledgerEventUtil;

    @Scheduled(fixedDelay = 3 * 60 * 1000)
    public void unsentEventsTask() {
        log.info("Unsent events task started");
        List<LedgerEventCreated> list = eventService.getUnSendedEvents();
        for (LedgerEventCreated ledgerEventCreated : list) {
            LedgerEventResult r = ledgerEventUtil.sendEvent(ledgerEventCreated);
            if (r.isOk()) {
                eventService.handleSendedEvent(ledgerEventCreated.getEventId());
            }
        }
    }
}
