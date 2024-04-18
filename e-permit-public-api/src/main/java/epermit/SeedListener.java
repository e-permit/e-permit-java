package epermit;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import epermit.services.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SeedListener {
    private final KeyService keyService;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        log.info("Seed database started");
        keyService.seed();
    }
}
