package epermit.events.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.JsonUtil;
import epermit.common.PermitActivityType;
import epermit.entities.IssuedPermit;
import epermit.events.EventHandleResult;
import epermit.repositories.IssuedPermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitUsedEventHandlerTest {
    @Mock
    IssuedPermitRepository permitRepository;

    @Test
    void handleShouldWork() {
        IssuedPermit permit = new IssuedPermit();
        permit.setId(Long.valueOf(1));
        permit.setIssuedFor("TR");
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.of(permit));
        PermitUsedEventHandler handler = new PermitUsedEventHandler(permitRepository);
        PermitUsedEvent event = PermitUsedEvent.builder().permitId("UA-TR-2021-1-1")
                .activityType(PermitActivityType.ENTERANCE).build();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isSucceed());
    }

    @Test
    void handleShouldReturnInvalidPermitId() {
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.empty());
        PermitUsedEventHandler handler = new PermitUsedEventHandler(permitRepository);
        PermitUsedEvent event = PermitUsedEvent.builder().permitId("UA-TR-2021-1-1").build();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
    }

    @Test
    void handleShouldReturn() {
        IssuedPermit permit = new IssuedPermit();
        permit.setId(Long.valueOf(1));
        permit.setIssuedFor("TR");
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.of(permit));
        PermitUsedEventHandler handler = new PermitUsedEventHandler(permitRepository);
        PermitUsedEvent event = PermitUsedEvent.builder().permitId("UA-TR-2021-1-1").build();
        event.setIssuer("TR2");
        event.setIssuedFor("UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("INVALID_PERMIT_ISSUER", r.getErrorCode());
    }
}
