package epermit.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.CreatedEvent;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.CreatedEventRepository;
import epermit.utils.JwsUtil;

@ExtendWith(MockitoExtension.class)
public class CreatedEventServiceTest {
    @Mock
    CreatedEventRepository createdEventRepository;

    @Mock
    JwsUtil jwsUtil;

    @InjectMocks
    CreatedEventService createdEventService;

    @Test
    void getEventsTest() {
        CreatedEvent createdEvent = new CreatedEvent();
        createdEvent.setId(Long.valueOf(1));
        createdEvent.setJws("jws");
        when(createdEventRepository.findOneByEventIdAndIssuedFor("1", "TR"))
                .thenReturn(Optional.of(createdEvent));
        when(createdEventRepository.findByIdGreaterThanOrderByIdAsc(Long.valueOf(1)))
                .thenReturn(List.of(createdEvent));
        when(jwsUtil.validateJws("requestJws"))
                .thenReturn(JwsValidationResult.success(Map.of("event_id", "1", "issuer", "TR")));
        List<String> r = createdEventService.getEvents("requestJws");
        assertEquals(List.of("jws"), r);
    }

    @Test
    void getEventsInvalidJwsTest() {
        when(jwsUtil.validateJws("requestJws")).thenReturn(JwsValidationResult.fail("INVALI_JWS"));
        assertThrows(ResponseStatusException.class, () -> {
            createdEventService.getEvents("requestJws");
        });
    }

    @Test
    void getEventsInvalidEventIdTest() {
        when(createdEventRepository.findOneByEventIdAndIssuedFor("1", "TR"))
                .thenReturn(Optional.empty());
        when(jwsUtil.validateJws("requestJws"))
                .thenReturn(JwsValidationResult.success(Map.of("event_id", "1", "issuer", "TR")));
        assertThrows(ResponseStatusException.class, () -> {
            createdEventService.getEvents("requestJws");
        });
    }
}
