package epermit.ledger.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.repositories.AuthorityRepository;
import epermit.ledger.repositories.PrivateKeyRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {
    private String jwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    PrivateKeyRepository keyRepository;

    @Spy
    ModelMapper modelMapper;

    @Mock
    EPermitProperties properties;

    @InjectMocks
    AuthorityService authorityService;

    /*@Test
    @SneakyThrows
    void getAllTest() {
        Authority authority = new Authority();
        authority.setCode("UA");
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        List<AuthorityDto> dtos = authorityService.getAll();
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeTest() {
        Authority authority = new Authority();
        authority.setCode("UA");
        when(authorityRepository.findOneByCode("UA")).thenReturn(authority);
        AuthorityDto dto = authorityService.getByCode("UA");
        assertEquals("UA", dto.getCode());
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("apiUri");
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("UZ");
        config.setName("Uzbekistan");
        config.setVerifyUri("VerifyUri");
        PublicJwk publicJwk = GsonUtil.getGson().fromJson(jwk, PublicJwk.class);
        config.setKeys(List.of(publicJwk));
        authorityService.create(input, config);
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        authority.setVerifyUri("VerifyUri");
        authority.setApiUri("apiUri");
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setKeyId("1");
        authorityKey.setJwk(jwk);
        authority.addKey(authorityKey);
        verify(authorityRepository, times(1)).save(authority);
    }

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        input.setAuthorityCode("TR");
        input.setEndId(20);
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setStartId(1);
        Authority authority = new Authority();
        when(authorityRepository.findOneByCode("TR")).thenReturn(authority);
        authorityService.createQuota(input);
        VerifierQuota verifierQuota = new VerifierQuota();
        verifierQuota.setEndNumber(20);
        verifierQuota.setPermitType(PermitType.BILITERAL);
        verifierQuota.setPermitYear(2021);
        verifierQuota.setStartNumber(1);
        authority.addVerifierQuota(verifierQuota);
        verify(authorityRepository, times(1)).save(authority);
    }

    @Test
    void createQuotaThrowTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        input.setAuthorityCode("TR");
        assertThrows(NullPointerException.class, () -> {
            authorityService.createQuota(input);
        });
    }

    @Test
    void enableQuotaTest() {
        VerifierQuota verifierQuota = new VerifierQuota();
        verifierQuota.setEndNumber(20);
        verifierQuota.setPermitType(PermitType.BILITERAL);
        verifierQuota.setPermitYear(2021);
        verifierQuota.setStartNumber(1);
        when(verifierQuotaRepository.findById(1)).thenReturn(Optional.of(verifierQuota));
        authorityService.enableQuota(1);
        assertTrue(verifierQuota.isActive());
        VerifierQuota expectedVerifierQuota = new VerifierQuota();
        expectedVerifierQuota.setEndNumber(20);
        expectedVerifierQuota.setPermitType(PermitType.BILITERAL);
        expectedVerifierQuota.setPermitYear(2021);
        expectedVerifierQuota.setStartNumber(1);
        expectedVerifierQuota.setActive(true);
        verify(verifierQuotaRepository, times(1)).save(expectedVerifierQuota);
        verify(quotaCreatedEventFactory, times(1)).create(expectedVerifierQuota);
    }

    @Test
    void enableQuotaThrowTest() {
        when(verifierQuotaRepository.findById(1)).thenReturn(Optional.empty());;
        assertThrows(ResponseStatusException.class, () -> {
            authorityService.enableQuota(1);
        });
    }*/

}
