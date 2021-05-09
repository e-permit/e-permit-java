package epermit.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.repositories.AuthorityRepository;

@Component
public class AppEventPublisher {
    private final AuthorityRepository authorityRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AppEventPublisher(AuthorityRepository authorityRepository, ApplicationEventPublisher eventPublisher) {
        this.authorityRepository = authorityRepository;
        this.eventPublisher = eventPublisher;
    }

    public void publish(CreatedEvent event){
       Authority authority = authorityRepository.findOneByCode(event.getIssuedFor()).get();
       AppEvent appEvent = new AppEvent();
       appEvent.setJws(event.getJws());
       appEvent.setUri(authority.getApiUri());
       eventPublisher.publishEvent(appEvent);
    }
}
