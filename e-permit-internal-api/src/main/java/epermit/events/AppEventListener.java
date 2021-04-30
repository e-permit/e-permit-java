package epermit.events;

import javax.print.attribute.HashDocAttributeSet;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import epermit.common.EventState;
import epermit.entities.CreatedEvent;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import lombok.extern.slf4j.Slf4j;

@EnableAsync(proxyTargetClass = true)
@Component
@Slf4j
public class AppEventListener {
    private final AppEventService service;

    public AppEventListener(AppEventService service) {
        this.service = service;
    }

    @Async
    @EventListener
    public void onAppEvent(AppEvent event) {
        service.send(event.getUri(), event.getJws());
        service.handle();
        log.debug("received ticket updated event");
    }
}
