package epermit.ledgerevents.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.models.enums.PermitType;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.PermitUtil;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedLedgerEventHandlerTest {
    @Mock
    PermitUtil permitUtil;

    @Mock
    LedgerPermitRepository permitRepository;

    @InjectMocks
    PermitCreatedLedgerEventHandler handler;

    @Captor
    ArgumentCaptor<LedgerPermit> captor;

    @Test
    void handleOkTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("TR", "UZ", "0");
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("UZ-TR-2021-1-1");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        when(permitUtil.getPermitId(any())).thenReturn("UZ-TR-2021-1-1");
        when(permitRepository.existsByPermitId("UZ-TR-2021-1-1")).thenReturn(false);
        LedgerEventHandleResult r = handler.handle(event);
        assertTrue(r.isOk());
        assertNull(r.getErrorCode());
        verify(permitRepository).save(captor.capture());
        LedgerPermit p = captor.getValue();
        assertEquals("A", p.getExpireAt());
        assertEquals("UZ-TR-2021-1-1", p.getPermitId());
        assertEquals(PermitType.BILITERAL, p.getPermitType());
        assertEquals("A", p.getCompanyName());
        assertEquals("A", p.getIssuedAt());
        assertEquals("UA", p.getIssuer());
        assertEquals(2021, p.getPermitYear());
        assertEquals(1, p.getSerialNumber());
    }

    /*
     * @Test void okTest() { when(permitRepository.existsByIssuerAndPermitId("UA",
     * "UA-TR-2021-1-1")).thenReturn(false); when(permitUtil.isQuotaSufficient("UA", 2021, 1,
     * PermitType.BILITERAL)).thenReturn(true); when(permitUtil.getPermitId("UA", "TR",
     * PermitType.BILITERAL, 2021, 1)) .thenCallRealMethod(); EventValidationResult r =
     * validator.validate(getPermitCreatedEvent("UA-TR-2021-1-1")); assertTrue(r.isOk()); }
     * 
     * @Test void invalidPermitIdTest() { when(permitUtil.getPermitId("UA", "TR",
     * PermitType.BILITERAL, 2021, 1)) .thenCallRealMethod(); EventValidationResult r =
     * validator.validate(getPermitCreatedEvent("UA-TR-2021-2")); assertFalse(r.isOk());
     * assertEquals("INVALID_PERMITID", r.getErrorCode()); }
     * 
     * @Test void permitExistTest() { when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL,
     * 2021, 1)) .thenCallRealMethod(); when(permitRepository.existsByIssuerAndPermitId("UA",
     * "UA-TR-2021-1-1")).thenReturn(true); EventValidationResult r =
     * validator.validate(getPermitCreatedEvent("UA-TR-2021-1-1")); assertFalse(r.isOk());
     * assertEquals("PERMIT_EXIST", r.getErrorCode()); }
     * 
     * @Test void insufficientQuotaTest() { when(permitUtil.getPermitId("UA", "TR",
     * PermitType.BILITERAL, 2021, 1)) .thenCallRealMethod();
     * when(permitRepository.existsByIssuerAndPermitId("UA", "UA-TR-2021-1-1")).thenReturn(false);
     * when(permitUtil.isQuotaSufficient("UA", 2021, 1, PermitType.BILITERAL)) .thenReturn(false);
     * 
     * EventValidationResult r = validator.validate(getPermitCreatedEvent("UA-TR-2021-1-1"));
     * assertFalse(r.isOk()); assertEquals("INSUFFICIENT_QUOTA", r.getErrorCode()); }
     * 
     * private Map<String, Object> getPermitCreatedEvent(String permitId) { PermitCreatedEvent event
     * = new PermitCreatedEvent(); event.setExpireAt("A"); event.setIssuedAt("A");
     * event.setCompanyName("A"); event.setPermitId(permitId);
     * event.setPermitType(PermitType.BILITERAL); event.setPermitYear(2021);
     * event.setPlateNumber("A"); event.setSerialNumber(1); event.setIssuer("UA");
     * event.setIssuedFor("TR"); return GsonUtil.toMap(event); }
     * 
     */
}
