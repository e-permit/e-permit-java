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
import epermit.events.EventValidationResult;
import epermit.models.PermitType;
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
        String payload = getPermitCreatedEvent("UA-TR-2021-1-1");
        EventValidationResult r = validator.validate(payload);
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdTest() {
        when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL, 2021, 1))
                .thenCallRealMethod();
        String payload = getPermitCreatedEvent("UA-TR-2021-2");
        EventValidationResult r = validator.validate(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
    }

    @Test
    void permitExistTest() {
        when(permitUtil.getPermitId("UA", "TR", PermitType.BILITERAL, 2021, 1))
                .thenCallRealMethod();
        when(permitRepository.existsByIssuerAndPermitId("UA", "UA-TR-2021-1-1")).thenReturn(true);
        String payload = getPermitCreatedEvent("UA-TR-2021-1-1");
        EventValidationResult r = validator.validate(payload);
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

        String payload = getPermitCreatedEvent("UA-TR-2021-1-1");
        EventValidationResult r = validator.validate(payload);
        assertFalse(r.isOk());
        assertEquals("QUOTA_DOESNT_MATCH", r.getErrorCode());
    }

    private String getPermitCreatedEvent(String permitId) {
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
        return GsonUtil.getGson().toJson(event);
    }

}
