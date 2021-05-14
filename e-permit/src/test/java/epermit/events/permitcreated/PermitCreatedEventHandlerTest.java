package epermit.events.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import epermit.models.PermitType;
import epermit.services.AuthorityService;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedEventHandlerTest {
    @Mock
    PermitService permitService;

    @Mock
    AuthorityService authorityService;

    @InjectMocks
    PermitCreatedEventHandler handler;

    @Test
    void okTest() {
        when(permitService.isIssuedPermitExist("TR", "UA-TR-")).thenReturn(true);
        when(authorityService.isQuotaSufficient()).thenReturn(true);
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("UA-TR-2021-1-1");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isOk());
        verify(permitService, times(1)).handlePermitCreated(event);
    }

    @Test
    void invalidPermitIdTest() {
        when(permitService.isIssuedPermitExist("TR", "UA-TR-")).thenReturn(true);
        when(authorityService.isQuotaSufficient()).thenReturn(true);
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("UA-TR-2021-1-1");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
        verify(permitService, never()).handlePermitCreated(event);
    }

    @Test
    void permitExistTest() {
        when(permitService.isIssuedPermitExist("TR", "UA-TR-")).thenReturn(true);
        when(authorityService.isQuotaSufficient()).thenReturn(true);
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("UA-TR-2021-1-1");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("PERMIT_EXIST", r.getErrorCode());
        verify(permitService, never()).handlePermitCreated(event);
    }

    @Test
    void insufficientQuotaTest() {
        when(permitService.isIssuedPermitExist("TR", "UA-TR-")).thenReturn(true);
        when(authorityService.isQuotaSufficient()).thenReturn(true);
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("UA-TR-2021-1-1");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("QUOTA_DOESNT_MATCH", r.getErrorCode());
        verify(permitService, never()).handlePermitCreated(event);
    }

}
