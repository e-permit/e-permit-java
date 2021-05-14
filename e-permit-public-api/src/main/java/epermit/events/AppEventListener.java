package epermit.events;

import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableAsync(proxyTargetClass = true)
public class AppEventListener {

    private final EventService eventService;

    @Async
    @EventListener
    public void onAppEvent(AppEvent event) {
        log.info("Event is received jws: " + event.getJws());
        EventHandleResult r = eventService.handle(event.getJws());
        if (!r.isSucceed() && r.getErrorCode().equals("NOTEXIST_PREVIOUSEVENT")) {
            List<String> jwsList = eventService.getEvents(event.getJws());
            jwsList.forEach(jws -> {
                eventService.handle(event.getJws());
            });
        }
    }
}
