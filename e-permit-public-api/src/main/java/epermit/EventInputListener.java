package epermit;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync(proxyTargetClass = true)
@Component
public class EventInputListener {

    private final EventService service;
    public EventInputListener(EventService service) {
       this.service = service;
    }

    @Async
    @EventListener
    public void onEventInputReceived(EventInput event) {
        service.handle(event.getJws());
    }
}