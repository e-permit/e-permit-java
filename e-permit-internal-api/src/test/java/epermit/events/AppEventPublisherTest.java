package epermit.events;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class AppEventPublisherTest {
    @Mock AuthorityRepository authorityRepository;
    @Mock ApplicationEventPublisher eventPublisher;


    @Test
    void test(){
        CreatedEvent createdEvent = new CreatedEvent();
        createdEvent.setJws("jws");
        createdEvent.setIssuedFor("TR");
        Authority authority = new Authority();
        authority.setApiUri("uri");
        when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.of(authority));
        AppEventPublisher publisher = new AppEventPublisher(authorityRepository, eventPublisher);
        publisher.publish(createdEvent);
        AppEvent event = new AppEvent();
        event.setUri("uri");
        event.setJws("jws");
        verify(eventPublisher).publishEvent(event);
    }
    
}
