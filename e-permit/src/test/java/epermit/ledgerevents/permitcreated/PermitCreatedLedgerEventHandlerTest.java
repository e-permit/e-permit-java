package epermit.ledgerevents.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPermit;
import epermit.models.enums.PermitType;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.PermitUtil;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedLedgerEventHandlerTest {

    private static Validator validator;

    @Mock
    PermitUtil permitUtil;

    @Mock
    LedgerPermitRepository permitRepository;

    @InjectMocks
    PermitCreatedLedgerEventHandler handler;

    @Captor
    ArgumentCaptor<LedgerPermit> captor;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void handleValidationTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("UZ", "TR", "0");
        event.setExpireAt("03/03/2021");
        event.setIssuedAt("03/02/2021");
        event.setCompanyName("A");
        event.setCompanyId("companyId");
        event.setPermitId("UZ-TR-2021-1-1");
        event.setPermitIssuedFor("TR");
        event.setPermitIssuer("UZ");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setQrCode("qrCode");
        Set<ConstraintViolation<PermitCreatedLedgerEvent>> constraintViolations =
                validator.validate(event);
        assertEquals(constraintViolations.size(), 0);
    }

    @Test
    void handleInvalidPermitIdTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("UZ", "TR", "0");
        event.setPermitId("UZ-TR-2021-1-1");
        when(permitUtil.getPermitId(any())).thenReturn("UZ-TR-2021-1-2");
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(event);
                });
        assertEquals(ErrorCodes.INVALID_PERMITID.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handlePermitIdAlreadyExistsTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("UZ", "TR", "0");
        event.setPermitId("UZ-TR-2021-1-1");
        when(permitUtil.getPermitId(any())).thenReturn("UZ-TR-2021-1-1");
        when(permitRepository.existsByPermitId(event.getPermitId())).thenReturn(true);
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(event);
                });
        assertEquals(ErrorCodes.PERMITID_ALREADY_EXISTS.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handleInsufficientPermitQuotaTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("UZ", "TR", "0");
        event.setPermitId("UZ-TR-2021-1-1");
        when(permitUtil.getPermitId(any())).thenReturn("UZ-TR-2021-1-1");
        when(permitRepository.existsByPermitId(event.getPermitId())).thenReturn(false);
        when(permitUtil.isQuotaSufficient(any())).thenReturn(false);
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(event);
                });
        assertEquals(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handleOkTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("UZ", "TR", "0");
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
        when(permitUtil.isQuotaSufficient(any())).thenReturn(true);
        handler.handle(event);
        verify(permitRepository).save(captor.capture());
        LedgerPermit p = captor.getValue();
        assertEquals("A", p.getExpireAt());
        assertEquals("UZ-TR-2021-1-1", p.getPermitId());
        assertEquals(PermitType.BILITERAL, p.getPermitType());
        assertEquals("A", p.getCompanyName());
        assertEquals("A", p.getIssuedAt());
        assertEquals("UZ", p.getIssuer());
        assertEquals("TR", p.getIssuedFor());
        assertEquals(2021, p.getPermitYear());
        assertEquals(1, p.getSerialNumber());
    }

}
