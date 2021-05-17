package epermit.listeners;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.support.TransactionTemplate;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeedEventListener {
    private final KeyService keyService;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        
    }
}
