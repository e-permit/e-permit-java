package epermit.utils;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.models.EPermitProperties;
import epermit.models.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class PermitUtilTest {
    @Mock
    JwsUtil jwsUtil;

    @Mock
    EPermitProperties properties;

    @Mock
    IssuedPermitRepository issuedPermitRepository;
    
    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    PermitUtil util;

    @Test
    void getPermitIdTest() {
        String permitId = util.getPermitId("TR", "UA", PermitType.BILITERAL, 2021, 12);
        assertEquals("TR-UA-2021-1-12", permitId);
    }

    @Test
    @SneakyThrows
    void gneratePermitIdTest() {
        IssuedPermit permit = new IssuedPermit();
        permit.setId((long) 1);
        permit.setSerialNumber(1);
        when(issuedPermitRepository.findFirstByIssuedForAndRevokedTrue("UA")).thenReturn(Optional.of(permit));
        Integer nextPid = util.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        verify(issuedPermitRepository, times(1)).delete(permit);
        Assertions.assertEquals(1, nextPid);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void gneratePermitIdParameterizedTest(int endNumber) {
        Authority authority = new Authority();
        IssuerQuota quota = new IssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setActive(true);
        quota.setPermitYear(2021);
        quota.setCurrentNumber(1);
        quota.setEndNumber(endNumber);
        authority.addIssuerQuota(quota);
        when(issuedPermitRepository.findFirstByIssuedForAndRevokedTrue("UA")).thenReturn(Optional.empty());
        when(authorityRepository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        Integer nextPid = util.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        Assertions.assertEquals(2, nextPid);
        Assertions.assertEquals(endNumber == 3, quota.isActive());
    }

    /*@Test
    void createPermitTest() {
        when(properties.getIssuerCode()).thenReturn("UA");
        when(util.generateSerialNumber("TR", 2021, PermitType.BILITERAL)).thenReturn(1);
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyName("companyName");
        input.setIssuedFor("TR");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("plateNumber");
        Map<String, Object> claims = new HashMap<>();
        claims.put("key", "value");
        input.setClaims(claims);
        IssuedPermit permit = util.createPermit(input);
        assertNotNull(permit);
        assertEquals("companyName", permit.getCompanyName());
        assertEquals("UA-TR-2021-1-1", permit.getPermitId());
    }*/
}
