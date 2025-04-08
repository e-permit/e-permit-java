package epermit;

import java.util.List;
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

    @Scheduled(fixedDelay = 3 * 60 * 1000)
    @SneakyThrows
    public void unsentEventsTask() {
        List<LedgerEventCreated> list = eventService.getUnSentEvents();
        if(!list.isEmpty()){
           log.info(String.format("Found %d unsended events", list.size()));
        }
        for (LedgerEventCreated event : list) {
            try {
                eventService.sendEvent(event);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
