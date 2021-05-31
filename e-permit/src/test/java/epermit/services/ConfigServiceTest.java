package epermit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.events.quotacreated.QuotaCreatedEventFactory;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.repositories.VerifierQuotaRepository;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {
    private String jwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";


    @Mock
    KeyRepository keyRepository;

    @Spy
    ModelMapper modelMapper;

    @Mock
    EPermitProperties properties;

    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    ConfigService configService;
    @Test
    void getConfigTest() {
        when(properties.getIssuerCode()).thenReturn("TR");
        when(properties.getIssuerVerifyUri()).thenReturn("VeirfyUri");
        Key key = new Key();
        key.setKeyId("1");
        key.setEnabled(true);
        key.setPublicJwk(jwk);
        Authority authority = new Authority();
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setKeyId("1");
        authorityKey.setJwk(jwk);
        authority.addKey(authorityKey);
        when(keyRepository.findAllByEnabledTrue()).thenReturn(List.of(key));
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        AuthorityConfig config = configService.getConfig();
        assertNotNull(config);
        assertEquals("TR", config.getCode());
        assertEquals(1, config.getKeys().size());
        assertEquals(1, config.getTrustedAuthorities().size());
    }
}
