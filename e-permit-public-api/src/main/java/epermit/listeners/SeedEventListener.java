package epermit.listeners;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import epermit.services.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SeedEventListener {
    private final ConfigService configService;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        log.info("Seed database started");
        configService.seed();
    }
}
