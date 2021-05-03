package epermit.events;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@EnableAsync(proxyTargetClass = true)
@Component
public class EventInputListener {

    private final EventDispatcher dispatcher;

    public EventInputListener(EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Async
    @EventListener
    public void onEventInputReceived(EventInput event) {
        dispatcher.dispatch(event.getJws());
    }
}