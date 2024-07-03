package epermit.ledgerevents.permitrevoked;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("B", "A", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("B-A-2021-1-1");
        p.setIssuer("B");
        p.setIssuedFor("A");
        p.setPermitType(1);
        p.setPermitYear(2021);
        when(quotaRepository.findOneByParams(anyString(), anyString(), anyInt(), anyInt() ))
                .thenReturn(Optional.of(LedgerQuota.builder().totalQuota(5L).build()));
        
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        handler.handle(event);
        verify(permitRepository, times(1)).save(p);
    }

    @Test
    void handlePermitNotFoundTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("B", "A", "0");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.empty());
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).delete(any(LedgerPermit.class));
    }

    @Test
    void handleInvalidIssuerTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("B", "A", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("B-A-2021-1-1");
        p.setIssuer("B2");
        p.setIssuedFor("A");
        p.setPermitType(1);
        p.setPermitYear(2021);
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).delete(any(LedgerPermit.class));
    }

    @Test
    void handleInvalidIssuedForTest() {
        PermitRevokedLedgerEvent event = new PermitRevokedLedgerEvent("B", "A", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("B-A-2021-1-1");
        p.setIssuer("B");
        p.setIssuedFor("A2");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).delete(any(LedgerPermit.class));
    }

}
