package epermit.ledgerevents.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import epermit.repositories.LedgerPermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitUsedLedgerEventHandlerTest {

    @Mock
    LedgerPermitRepository permitRepository;
    
    @InjectMocks
    PermitUsedLedgerEventHandler handler;

    @Test
    void handleOkTest() {
        PermitUsedLedgerEvent event = new PermitUsedLedgerEvent("A", "B", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("B-A-2021-1-1");
        p.setIssuer("B");
        p.setIssuedFor("A");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        handler.handle(event);
        verify(permitRepository, times(1)).save(any());
        assertTrue(p.isUsed());
        assertEquals(1, p.getActivities().size());
    }

    @Test
    void handlePermitNotFoundTest() {
        PermitUsedLedgerEvent event = new PermitUsedLedgerEvent("A", "B", "0");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.empty());
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(event);
                });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handleInvalidIssuerTest() {
        PermitUsedLedgerEvent event = new PermitUsedLedgerEvent("A", "B", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("B-A-2021-1-1");
        p.setIssuer("B2");
        p.setIssuedFor("A");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(event);
                });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }

    @Test
    void handleInvalidIssuedForTest() {
        PermitUsedLedgerEvent event = new PermitUsedLedgerEvent("A", "B", "0");
        LedgerPermit p = new LedgerPermit();
        p.setPermitId("B-A-2021-1-1");
        p.setIssuer("B");
        p.setIssuedFor("A2");
        when(permitRepository.findOneByPermitId(event.getPermitId())).thenReturn(Optional.of(p));
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(event);
                });
        assertEquals(ErrorCodes.PERMIT_NOTFOUND.name(), ex.getErrorCode());
        verify(permitRepository, never()).save(any());
    }
}
