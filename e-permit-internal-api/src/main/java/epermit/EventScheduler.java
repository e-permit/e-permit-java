package epermit;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import epermit.models.LedgerEventCreated;
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
        List<LedgerEventCreated> list = eventService.getUnSendedEvents();
        if(!list.isEmpty()){
           log.info(String.format("Found %d unsended events", list.size()));
        }
        for (LedgerEventCreated event : list) {
            eventPublisher.publishEvent(event);
            Thread.sleep(100);
            log.info( String.format("Event published id: %s", event.getEventId()));
        }
    }
}
