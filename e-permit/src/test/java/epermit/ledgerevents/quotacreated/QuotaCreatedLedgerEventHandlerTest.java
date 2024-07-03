package epermit.ledgerevents.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.LedgerQuota;
import epermit.models.EPermitProperties;
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
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent("A", "B", "0");
        event.setQuantity(4L);
        event.setPermitType(1);
        event.setPermitYear(2021);
        handler.handle(event);
        verify(quotaRepository, times(1)).save(captor.capture());
        LedgerQuota quota = captor.getValue();
        assertEquals(4, quota.getTotalQuota());
        assertEquals(2021, quota.getPermitYear());
        assertEquals(1, quota.getPermitType());
    }
}
