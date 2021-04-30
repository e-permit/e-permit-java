package epermit.events;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AppEventTask {
    private final AppEventService service;

    public AppEventTask(AppEventService service) {
        this.service = service;
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void handleEvents() {
       
    }
}

