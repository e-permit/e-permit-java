package epermit.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.entities.AuthorityEvent;
import epermit.repositories.AuthorityEventRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorityEventService {
    private final AuthorityEventRepository authorityEventRepository;

    @Transactional
    public void handleSendedEvent(String eventId) {
        AuthorityEvent authorityEvent = authorityEventRepository.findByEventId(eventId).get();
        authorityEvent.setSended(true);
        authorityEventRepository.save(authorityEvent);
    }
}
