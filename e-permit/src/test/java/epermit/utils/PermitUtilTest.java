package epermit.utils;


import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class PermitUtilTest {
    @Mock
    JwsUtil jwsUtil;

    @Mock
    EPermitProperties properties;

    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    PermitUtil util;

    @Test
    void getPermitIdTest() {
        CreatePermitIdInput input = new CreatePermitIdInput();
        input.setIssuedFor("UA");
        input.setIssuer("TR");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setSerialNumber(12);
        String permitId = util.getPermitId(input);
        assertEquals("TR-UA-2021-1-12", permitId);
    }

    @Test
    void getIssuerQuotaTest(){
       
    }

    /*@Test
    @SneakyThrows
    void gnerateSerialNumberTest() {
        //when(quotaRepository.fi)
        Optional<Integer> result = util.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
        Assertions.assertEquals(1, result.get());
    }*/


    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    void generateSerialNumberParameterizedTest(int endNumber) {
        /*
         * Authority authority = new Authority(); LedgerQuota quota = new LedgerQuota();
         * quota.setPermitType(PermitType.BILITERAL); quota.setActive(true);
         * quota.setPermitYear(2021); quota.setEndNumber(endNumber);
         * authority.addIssuerQuota(quota);
         * when(issuedPermitRepository.findFirstByIssuedForAndRevokedTrue("UA")).thenReturn(Optional
         * .empty()); when(authorityRepository.findOneByCode("UA")).thenReturn(authority);
         * Optional<Integer> result = util.generateSerialNumber("UA", 2021, PermitType.BILITERAL);
         * Assertions.assertEquals(2, result.get()); Assertions.assertEquals(endNumber == 3,
         * quota.isActive());
         */
    }
}
