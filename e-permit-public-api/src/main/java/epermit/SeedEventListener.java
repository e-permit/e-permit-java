package epermit;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.support.TransactionTemplate;
import epermit.entities.Key;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class SeedEventListener {
    private final KeyService keyService;
    private final KeyRepository keyRepository;
    private final TransactionTemplate transactionTemplate;

    @EventListener
    @SneakyThrows
    public void seed(ContextRefreshedEvent event) {
        Long keyCount = keyRepository.count();
        if (keyCount == 0) {
              transactionTemplate.executeWithoutResult(x -> {
                Key key = keyService.create("1");
                keyRepository.save(key);
            });
        }
    }
}
