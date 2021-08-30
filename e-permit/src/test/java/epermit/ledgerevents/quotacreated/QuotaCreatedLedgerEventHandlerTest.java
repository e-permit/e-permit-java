package epermit.ledgerevents.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
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
        when(properties.getIssuerCode()).thenReturn("TR");
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent("TR", "UZ", "0");
        event.setStartNumber(4);
        event.setEndNumber(40);
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        handler.handle(GsonUtil.toMap(event));
        verify(quotaRepository, times(1)).save(captor.capture());
        LedgerQuota quota = captor.getValue();
        assertEquals(4, quota.getStartNumber());
        assertEquals(40, quota.getEndNumber());
        assertEquals(2021, quota.getPermitYear());
        assertEquals(PermitType.BILITERAL, quota.getPermitType());
    }

    @Test
    void handleInvalidQuotaIntervalTest() {
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent("TR", "UZ", "0");
        when(quotaRepository.count(any(Specification.class))).thenReturn(Long.valueOf(1));
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(GsonUtil.toMap(event));
                });
        assertEquals(ErrorCodes.INVALID_QUOTA_INTERVAL.name(), ex.getErrorCode());
    }
}
