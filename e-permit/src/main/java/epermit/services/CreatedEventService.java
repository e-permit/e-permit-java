package epermit.services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.CreatedEvent;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.CreatedEventRepository;
import epermit.utils.GsonUtil;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class CreatedEventService {
    private final CreatedEventRepository createdEventRepository;
    private final JwsUtil jwsUtil;

    @SneakyThrows
    public List<String> getEvents(String requestJws) {
        JwsValidationResult validationResult = jwsUtil.validateJws(requestJws);
        if(!validationResult.isValid()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_JWS");
        }
        Map<String, Object> claims = validationResult.getPayload();
        String issuer = (String)claims.get("issuer");
        String eventId = (String)claims.get("event_id");
        Optional<CreatedEvent> eventR =
                createdEventRepository.findOneByEventIdAndIssuedFor(eventId, issuer);
        if (eventR.isPresent()) {
            List<String> events =
                    createdEventRepository.findByIdGreaterThanOrderByIdAsc(eventR.get().getId())
                            .stream().map(x -> x.getJws()).collect(Collectors.toList());
            return events;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_EVENT_ID");
    }

}
