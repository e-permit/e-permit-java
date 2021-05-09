package epermit.events.permitrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.JsonUtil;
import epermit.entities.Permit;
import epermit.events.EventHandleResult;
import epermit.repositories.PermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedEventHandlerTest {
    @Mock
    PermitRepository permitRepository;

    @Test
    void handleShouldWork() {
        Permit permit = new Permit();
        permit.setId(Long.valueOf(1));
        permit.setIssuer("UA");
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.of(permit));
        PermitRevokedEventHandler handler =
                new PermitRevokedEventHandler(permitRepository);
                PermitRevokedEvent event =  PermitRevokedEvent.builder().permitId("UA-TR-2021-1-1").build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isSucceed());
    }
    
    @Test
    void handleShouldReturnInvalidPermitId() {
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.empty());
        PermitRevokedEventHandler handler =
                new PermitRevokedEventHandler(permitRepository);
                PermitRevokedEvent event =  PermitRevokedEvent.builder().permitId("UA-TR-2021-1-1").build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
    }

    @Test
    void handleShouldReturn() {
        Permit permit = new Permit();
        permit.setId(Long.valueOf(1));
        permit.setIssuer("UA2");
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.of(permit));
        PermitRevokedEventHandler handler =
                new PermitRevokedEventHandler(permitRepository);
                PermitRevokedEvent event =  PermitRevokedEvent.builder().permitId("UA-TR-2021-1-1").build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("INVALID_PERMIT_ISSUER", r.getErrorCode());
    }
}
