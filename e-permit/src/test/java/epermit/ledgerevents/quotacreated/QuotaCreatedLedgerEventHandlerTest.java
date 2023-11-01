package epermit.ledgerevents.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerQuota;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.repositories.LedgerQuotaRepository;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedLedgerEventHandlerTest {
    @Mock
    LedgerQuotaRepository quotaRepository;

    @Mock
    EPermitProperties properties;

    @InjectMocks
    QuotaCreatedLedgerEventHandler handler;

    @Captor
    ArgumentCaptor<LedgerQuota> captor;

    @Test
    void handleOkTest() {
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent("TR", "UZ", "0");
        event.setQuantity(4L);
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        handler.handle(event);
        verify(quotaRepository, times(1)).save(captor.capture());
        LedgerQuota quota = captor.getValue();
        assertEquals(4, quota.getBalance());
        assertEquals(1, quota.getNextSerial());
        assertEquals(0, quota.getSpent());
        assertEquals(2021, quota.getPermitYear());
        assertEquals(PermitType.BILITERAL, quota.getPermitType());
    }

    @Test
    void handleInvalidQuotaIntervalTest() {
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent("TR", "UZ", "0");
        when(quotaRepository.exists(ArgumentMatchers.<Specification<LedgerQuota>>any()))
                .thenReturn(true);
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(event);
        });
        assertEquals(ErrorCodes.INVALID_QUOTA_INTERVAL.name(), ex.getErrorCode());
    }
}
