package epermit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.AuthorityIssuerQuota;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerPermitActivity;
import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class SerialNumberUtilTest {

    @Mock
    PermitUtil permitUtil;

    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    SerialNumberUtil util;

    @Test
    void generateAvailableSerialNumberTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("TR");
        authority.setName("name");
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        quota.setAvailableSerialNumbers(GsonUtil.getGson().toJson(List.of(10)));
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        Integer serialNumber = util.generate("UZ", 2021, PermitType.BILITERAL);
        assertEquals(10, serialNumber);
    }

    @Test
    void generateTest() {
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        /*AuthorityIssuerQuotaPayload payload = new AuthorityIssuerQuotaPayload();
        payload.setAvailableSerialNumbers(List.of());
        quota.setPayload(payload);
        LedgerQuota ledgerQuota = new LedgerQuota();
        
        when(permitUtil.getIssuerQuota("UZ", PermitType.BILITERAL, 2021)).thenReturn(quota);
        when(permitUtil.getActiveLedgerQuota(payload, "UZ", PermitType.BILITERAL, 2021)).thenReturn(value)
        Integer serialNumber = util.generate("UZ", 2021, PermitType.BILITERAL);
        assertEquals(10, serialNumber);*/
    }
}
