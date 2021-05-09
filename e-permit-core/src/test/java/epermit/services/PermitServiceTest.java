package epermit.services;


import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.PermitProperties;
import epermit.common.PermitType;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class PermitServiceTest {

    @Mock  AuthorityRepository authorityRepository;
    @Mock IssuedPermitRepository issuedCredentialRepository;
    @Mock PermitProperties props;
    @Mock KeyService keyService;

    //@Test
    @SneakyThrows
    void gneratePermitIdTest() {
        IssuedPermit permit = new IssuedPermit();
        permit.setId((long) 1);
        permit.setSerialNumber(1);
        PermitService service = new PermitService(authorityRepository, issuedCredentialRepository, props, keyService);
        when(issuedCredentialRepository.findFirstByRevokedTrue()).thenReturn(Optional.of(permit));
        Integer nextPid = service.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        verify(issuedCredentialRepository, times(1)).delete(permit);
        Assertions.assertEquals(1, nextPid);
    }

    //@Test
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void gneratePermitIdTest2(int endNumber) {
        Authority authority = new Authority();
        IssuerQuota quota = new IssuerQuota();
        quota.setPermitType(PermitType.BILITERAL);
        quota.setActive(true);
        quota.setPermitYear(2021);
        quota.setCurrentNumber(1);
        quota.setEndNumber(endNumber);
        authority.addIssuerQuota(quota);
        PermitService service = new PermitService(authorityRepository, issuedCredentialRepository, props, keyService);
        when(issuedCredentialRepository.findFirstByRevokedTrue()).thenReturn(Optional.empty());
        when(authorityRepository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        Integer nextPid = service.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        Assertions.assertEquals(2, nextPid);
        Assertions.assertEquals(endNumber == 3, quota.isActive());
    }
}

