package epermit.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class CreatedEventService {
    private final CreatedEventRepository createdEventRepository;

    @SneakyThrows
    public List<String> getEvents(Map<String, Object> claims) {
        String issuer = (String) claims.get("issuer");
        String lastEventId = (String) claims.get("last_event_id");
        Optional<CreatedEvent> eventR =
                createdEventRepository.findOneByEventIdAndIssuedFor(lastEventId, issuer);
        if (eventR.isPresent()) {
            List<CreatedEvent> createdEvents =
                    createdEventRepository.findByIdGreaterThanOrderByIdAsc(eventR.get().getId());
            List<String> events =
                    createdEvents.stream().map(x -> x.getJws()).collect(Collectors.toList());
            return events;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_EVENT_ID");
    }

}
