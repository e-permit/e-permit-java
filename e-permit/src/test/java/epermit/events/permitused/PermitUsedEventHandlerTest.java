package epermit.events.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventHandleResult;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class PermitUsedEventHandlerTest {
    @Mock
    PermitService permitService;

    @InjectMocks
    PermitUsedEventHandler handler;

    @Captor
    ArgumentCaptor<PermitUsedEvent> eventCaptor;

    @Test
    void okTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = GsonUtil.getGson().toJson(event);
        when(permitService.isIssuedPermitExist(anyString(), anyString())).thenReturn(true);
        EventHandleResult r = handler.handle(payload);
        verify(permitService).handlePermitUsed(eventCaptor.capture());
        PermitUsedEvent e = eventCaptor.getValue();
        assertEquals(e.getActivityType(), event.getActivityType());
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdOrIssuerTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = GsonUtil.getGson().toJson(event);
        when(permitService.isIssuedPermitExist(anyString(), anyString())).thenReturn(false);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());
        verify(permitService, never()).handlePermitUsed(any());
    }

}
