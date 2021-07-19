package epermit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.AuthorityIssuerQuota;
import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityIssuerQuotaRepository;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class SerialNumberUtilTest {

    @Mock
    PermitUtil permitUtil;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    AuthorityIssuerQuotaRepository authorityIssuerQuotaRepository;

    @InjectMocks
    SerialNumberUtil util;

    @Test
    void generateAvailableSerialNumberTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        quota.addSerialNumber(10);
        //quota.setAvailableSerialNumbers(GsonUtil.getGson().toJson(List.of(10)));
        authority.addIssuerQuota(quota);
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        LedgerQuota ledgerQuota = new LedgerQuota();
        ledgerQuota.setId(1);
        //when(util.getLedgerQuota("UZ", PermitType.BILITERAL, 2021, List.of())).thenReturn(ledgerQuota);
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
