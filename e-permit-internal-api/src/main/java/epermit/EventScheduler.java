package epermit;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import epermit.repositories.AuthorityRepository;
import epermit.services.PersistedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class EventScheduler {

    private final PersistedEventService eventService;
    
    private final AuthorityRepository authorityRepository;

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void unsentEventsTask() {
        log.info("Unsent events task started");
        eventService.getUnsentEvents(issuedFor, eventId)
    }
}
