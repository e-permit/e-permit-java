package epermit.ledgerevents.permitrevoked;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedLedgerEventHandlerTest {
    @Mock
    LedgerPermitRepository permitRepository;

    @Mock
    LedgerQuotaRepository quotaRepository;

    @InjectMocks
    PermitRevokedLedgerEventHandler handler;

    @Test
    void handleOkTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("UZ", "TR", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("UZ-TR-2021-1-1");
        p.setIssuer("UZ");
        p.setIssuedFor("TR");
        when(quotaRepository.findOne(ArgumentMatchers.<Specification<LedgerQuota>>any()))
                .thenReturn(Optional.of(LedgerQuota.builder().balance(5L).build()));
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        handler.handle(event);
        verify(permitRepository, times(1)).delete(p);
    }

    @Test
    void handlePermitNotFoundTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("UZ", "TR", "0");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.empty());
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).delete(any(LedgerPermit.class));
    }

    @Test
    void handleInvalidIssuerTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("UZ", "TR", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("UZ-TR-2021-1-1");
        p.setIssuer("UZ2");
        p.setIssuedFor("TR");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).delete(any(LedgerPermit.class));
    }

    @Test
    void handleInvalidIssuedForTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("UZ", "TR", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("UZ-TR-2021-1-1");
        p.setIssuer("UZ");
        p.setIssuedFor("TR2");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).delete(any(LedgerPermit.class));
    }

}
