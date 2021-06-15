package epermit.ledger.ledgerevents.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventValidationResult;
import epermit.models.enums.PermitType;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;
import epermit.utils.PermitUtil;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedEventValidatorTest {
    @Mock
    PermitRepository permitRepository;

    @Mock
    PermitUtil permitUtil;

    @InjectMocks
    PermitCreatedEventValidator validator;

    @Test
    void okTest() {
        when(permitRepository.existsByIssuerAndPermitId("UA", "UA-TR-2021-1-1")).thenReturn(false);
        when(permitUtil.isQuotaSufficient("UA", 2021, 1, PermitType.BILITERAL)).thenReturn(true);
        when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL, 2021, 1))
                .thenCallRealMethod();
        EventValidationResult r = validator.validate(getPermitCreatedEvent("UA-TR-2021-1-1"));
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdTest() {
        when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL, 2021, 1))
                .thenCallRealMethod();
        EventValidationResult r = validator.validate(getPermitCreatedEvent("UA-TR-2021-2"));
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
    }

    @Test
    void permitExistTest() {
        when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL, 2021, 1))
                .thenCallRealMethod();
        when(permitRepository.existsByIssuerAndPermitId("UA", "UA-TR-2021-1-1")).thenReturn(true);
        EventValidationResult r = validator.validate(getPermitCreatedEvent("UA-TR-2021-1-1"));
        assertFalse(r.isOk());
        assertEquals("PERMIT_EXIST", r.getErrorCode());
    }

    @Test
    void insufficientQuotaTest() {
        when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL, 2021, 1))
                .thenCallRealMethod();
        when(permitRepository.existsByIssuerAndPermitId("UA", "UA-TR-2021-1-1")).thenReturn(false);
        when(permitUtil.isQuotaSufficient("UA", 2021, 1, PermitType.BILITERAL))
                .thenReturn(false);

        EventValidationResult r = validator.validate(getPermitCreatedEvent("UA-TR-2021-1-1"));
        assertFalse(r.isOk());
        assertEquals("INSUFFICIENT_QUOTA", r.getErrorCode());
    }

    private Map<String, Object> getPermitCreatedEvent(String permitId) {
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId(permitId);
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        return GsonUtil.toMap(event);
    }

}
