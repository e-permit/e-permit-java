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
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.TrustedAuthority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {
    private String jwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";

    @Spy
    ModelMapper modelMapper;

    @Mock
    EPermitProperties properties;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @InjectMocks
    ConfigService configService;


    @Test
    void getConfigTest() {
        when(properties.getIssuerCode()).thenReturn("TR");
        LedgerPublicKey key = new LedgerPublicKey();
        key.setKeyId("1");
        key.setJwk(jwk);
        when(ledgerPublicKeyRepository.findAllByAuthorityCodeAndRevokedFalse("TR")).thenReturn(List.of(key));
        AuthorityConfig config = configService.getConfig();
        assertNotNull(config);
        assertEquals("TR", config.getCode());
        assertEquals(1, config.getKeys().size());
    }

    @Test
    void getTrustedAuthoritiesTest() {
        LedgerPublicKey key = new LedgerPublicKey();
        key.setAuthorityCode("UZ");
        key.setKeyId("1");
        key.setJwk(jwk);
        Authority authority = new Authority();
        authority.setApiUri("api");
        authority.setCode("UZ");
        authority.setName("UZ");
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        when(ledgerPublicKeyRepository.findAllByAuthorityCodeAndRevokedFalse("UZ")).thenReturn(List.of(key));
        List<TrustedAuthority> trustedAuthorities = configService.getTrustedAuthorities();
        assertEquals(1, trustedAuthorities.size());
    }
}
