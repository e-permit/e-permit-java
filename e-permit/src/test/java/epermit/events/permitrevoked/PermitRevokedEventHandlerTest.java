package epermit.events.permitrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventHandleResult;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedEventHandlerTest {
    @Mock
    PermitService permitService;

    @InjectMocks
    PermitRevokedEventHandler handler;

    @Test
    void okTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        when(permitService.isPermitExist(anyString(), anyString())).thenReturn(true);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isOk());
        verify(permitService, times(1)).handlePermitRevoked(event);
    }

    @Test
    void invalidPermitIdTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        when(permitService.isPermitExist(anyString(), anyString())).thenReturn(true);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
        verify(permitService, never()).handlePermitRevoked(event);
    }

    @Test
    void invalidPermitIssuerTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMIT_ISSUER", r.getErrorCode());
        verify(permitService, never()).handlePermitRevoked(event);
    }
}
