package epermit.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityIssuerQuotaRepository;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerQuotaRepository;

@ExtendWith(MockitoExtension.class)
public class SerialNumberUtilTest {

    @Mock
    PermitUtil permitUtil;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    AuthorityIssuerQuotaRepository authorityIssuerQuotaRepository;
    
    @Mock
    LedgerQuotaRepository ledgerQuotaRepository;

    @Mock
    EPermitProperties properties;

    @InjectMocks
    SerialNumberUtil util;

    @Test
    void generateRevokedSerialNumberTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        quota.addRevokedSerialNumber(10);
        authority.addIssuerQuota(quota);
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        Integer serialNumber = util.generate("UZ", 2021, PermitType.BILITERAL);
        assertEquals(10, serialNumber);
    }

    @Test
    void generateNextNumberTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        quota.setNextNumber(5);
        authority.addIssuerQuota(quota);
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        Integer serialNumber = util.generate("UZ", 2021, PermitType.BILITERAL);
        assertEquals(5, serialNumber);
    }

    @Test
    void generateEndNumberTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        quota.setNextNumber(5);
        quota.setEndNumber(5);
        authority.addIssuerQuota(quota);
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        Integer serialNumber = util.generate("UZ", 2021, PermitType.BILITERAL);
        assertEquals(5, serialNumber);
        assertNull(quota.getNextNumber());
        assertNull(quota.getStartNumber());
        assertNull(quota.getEndNumber());
    }

    @Test
    void generateTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        AuthorityIssuerQuota quota = new AuthorityIssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        authority.addIssuerQuota(quota);
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        LedgerQuota ledgerQuota = new LedgerQuota();
        ledgerQuota.setActive(true);
        ledgerQuota.setEndNumber(5);
        ledgerQuota.setPermitIssuedFor("UZ");
        ledgerQuota.setPermitIssuer("TR");
        ledgerQuota.setPermitType(PermitType.BILITERAL);
        ledgerQuota.setPermitYear(2021);
        ledgerQuota.setStartNumber(1);
        when(properties.getIssuerCode()).thenReturn("TR");
        when(ledgerQuotaRepository.findAll()).thenReturn(List.of(ledgerQuota));
        Integer serialNumber = util.generate("UZ", 2021, PermitType.BILITERAL);
        assertEquals(1, serialNumber);
    }
}
