package epermit.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class CreatedEventService {
    private final CreatedEventRepository createdEventRepository;

    @SneakyThrows
    public List<String> getEvents(String requestJws) {
        Optional<CreatedEvent> eventR =
                createdEventRepository.findOneByEventIdAndIssuedFor("eventId", "issuedFor");
        if (eventR.isPresent()) {
            List<String> events =
                    createdEventRepository.findByIdGreaterThanOrderByIdAsc(eventR.get().getId())
                            .stream().map(x -> x.getJws()).collect(Collectors.toList());
            return events;
        }
        return null;
    }

}
