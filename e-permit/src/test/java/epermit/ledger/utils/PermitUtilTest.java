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
import epermit.models.enums.PermitType;
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
    void gnerateSerialNumberTest() {
        IssuedPermit permit = new IssuedPermit();
        permit.setId((long) 1);
        permit.setSerialNumber(1);
        when(issuedPermitRepository.findFirstByIssuedForAndRevokedTrue("UA")).thenReturn(Optional.of(permit));
        Optional<Integer> result= util.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        verify(issuedPermitRepository, times(1)).delete(permit);
        Assertions.assertEquals(1, result.get());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void generateSerialNumberParameterizedTest(int endNumber) {
        Authority authority = new Authority();
        IssuerQuota quota = new IssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setActive(true);
        quota.setPermitYear(2021);
        quota.setNextNumber(2);
        quota.setEndNumber(endNumber);
        authority.addIssuerQuota(quota);
        when(issuedPermitRepository.findFirstByIssuedForAndRevokedTrue("UA")).thenReturn(Optional.empty());
        when(authorityRepository.findOneByCode("UA")).thenReturn(authority);
        Optional<Integer> result = util.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        Assertions.assertEquals(2, result.get());
        Assertions.assertEquals(endNumber == 3, quota.isActive());
    }
}
