package epermit.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
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
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.entities.VerifierQuota;
import epermit.events.quotacreated.QuotaCreatedEventFactory;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.PublicJwk;
import epermit.models.dtos.PublicKey;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.repositories.VerifierQuotaRepository;
import epermit.utils.GsonUtil;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {
    private String publicJwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    VerifierQuotaRepository verifierQuotaRepository;

    @Mock
    QuotaCreatedEventFactory quotaCreatedEventFactory;

    @Mock
    KeyRepository keyRepository;

    @Spy
    ModelMapper modelMapper;

    @Mock
    EPermitProperties properties;

    @InjectMocks
    AuthorityService authorityService;

    @Test
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
        when(authorityRepository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        AuthorityDto dto = authorityService.getByCode("UA");
        assertEquals("UA", dto.getCode());
    }

    @Test
    void getConfigTest() {
        when(properties.getIssuerCode()).thenReturn("TR");
        when(properties.getIssuerTitle()).thenReturn("Turkey");
        when(properties.getIssuerVerifyUri()).thenReturn("VeirfyUri");
        Key key = new Key();
        key.setKeyId("1");
        key.setValidFrom(OffsetDateTime.now().toEpochSecond());
        key.setValidUntil(OffsetDateTime.now().toEpochSecond());
        key.setPublicJwk(publicJwk);
        Authority authority = new Authority();
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setKeyId("1");
        authorityKey.setValidFrom(OffsetDateTime.now().toEpochSecond());
        authorityKey.setValidUntil(OffsetDateTime.now().toEpochSecond());
        authorityKey.setJwk(publicJwk);
        authority.addKey(authorityKey);
        when(keyRepository.findAll()).thenReturn(List.of(key));
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        AuthorityConfig config = authorityService.getConfig();
        assertNotNull(config);
        assertEquals("TR", config.getCode());
        assertEquals("Turkey", config.getName());
        assertEquals(1, config.getKeys().size());
        assertEquals(1, config.getAuthorities().size());
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("apiUri");
        input.setCode("UZ");
        input.setName("Uzbekistan");
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("UZ");
        config.setVerifyUri("VerifyUri");
        PublicKey publicKey = new PublicKey();
        publicKey.setKeyId("1");
        publicKey.setValidFrom(OffsetDateTime.now().toEpochSecond());
        publicKey.setValidUntil(OffsetDateTime.now().toEpochSecond());
        publicKey.setJwk(GsonUtil.getGson().fromJson(publicJwk, PublicJwk.class));
        config.setKeys(List.of(publicKey));
        authorityService.create(input, config);
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        authority.setVerifyUri("VerifyUri");
        authority.setApiUri("apiUri");
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setKeyId("1");
        authorityKey.setValidFrom(OffsetDateTime.now().toEpochSecond());
        authorityKey.setValidUntil(OffsetDateTime.now().toEpochSecond());
        authorityKey.setJwk(publicJwk);
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
        when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.of(authority));
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
        when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
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
    }

}
