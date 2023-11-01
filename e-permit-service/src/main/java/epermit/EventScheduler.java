package epermit;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import epermit.appevents.LedgerEventCreated;
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
        List<LedgerEventCreated> list = eventService.getUnSendedEvents();
        log.info(String.format("Found %d unsended events", list.size()));
        for (LedgerEventCreated event : list) {
            eventPublisher.publishEvent(event);
            log.info( String.format("Event published id: %s", event.getEventId()));
        }
    }
}
