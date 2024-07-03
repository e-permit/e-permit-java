package epermit.ledgerevents.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

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
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.utils.PermitUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedLedgerEventHandlerTest {

    private static Validator validator;

    @Mock
    PermitUtil permitUtil;

    @Mock
    LedgerPermitRepository permitRepository;

    @Mock
    LedgerQuotaRepository quotaRepository;

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
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("B", "A", "0");
        event.setExpiresAt("03/03/2021");
        event.setIssuedAt("03/02/2021");
        event.setCompanyName("A");
        event.setCompanyId("companyId");
        event.setPermitId("B-A-2021-1-1");
        event.setPermitIssuedFor("A");
        event.setPermitIssuer("B");
        event.setPermitType(1);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setArrivalCountry("A");
        event.setDepartureCountry("A");
        event.setQrCode("A");
        Set<ConstraintViolation<PermitCreatedLedgerEvent>> constraintViolations = validator.validate(event);
        assertEquals(0, constraintViolations.size());
    }

    @Test
    void handlePermitIdAlreadyExistsTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("B", "A", "0");
        event.setPermitId("B-A-2021-1-1");
        when(permitRepository.existsByPermitId(event.getPermitId())).thenReturn(true);
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMITID_ALREADY_EXISTS.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handleInsufficientPermitQuotaTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("B", "A", "0");
        event.setPermitId("B-A-2021-1-1");
        when(permitRepository.existsByPermitId(event.getPermitId())).thenReturn(false);
        
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handleOkTest() {
        PermitCreatedLedgerEvent event = new PermitCreatedLedgerEvent("B", "A", "0");
        event.setExpiresAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("B-A-2021-1-1");
        event.setPermitType(1);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setPermitIssuer("B");
        event.setPermitIssuedFor("A");
        when(permitRepository.existsByPermitId("B-A-2021-1-1")).thenReturn(false);
        when(quotaRepository.findOneByParams(anyString(), anyString(), anyInt(), anyInt() ))
                .thenReturn(Optional.of(LedgerQuota.builder().totalQuota(5L).build()));
        handler.handle(event);
        verify(permitRepository).save(captor.capture());
        LedgerPermit p = captor.getValue();
        assertEquals("A", p.getExpiresAt());
        assertEquals("B-A-2021-1-1", p.getPermitId());
        assertEquals(1, p.getPermitType());
        assertEquals("A", p.getCompanyName());
        assertEquals("A", p.getIssuedAt());
        assertEquals("B", p.getIssuer());
        assertEquals("A", p.getIssuedFor());
        assertEquals(2021, p.getPermitYear());
    }

}
