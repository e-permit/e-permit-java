package epermit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {
    private String jwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";


    @Mock
    PrivateKeyRepository keyRepository;

    @Spy
    ModelMapper modelMapper;

    @Mock
    EPermitProperties properties;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    private PrivateKeyUtil keyUtil;

    @InjectMocks
    ConfigService configService;

    /*@Test
    void seedTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(0));
        Key key = new Key();
        when(keyUtil.create("1")).thenReturn(key);
        configService.seed();
        verify(keyRepository).save(key);
    }

    @Test
    void seedKeyExistTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(1));
        configService.seed();
        verify(keyRepository, never()).save(any());
    }

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
    }*/
}
