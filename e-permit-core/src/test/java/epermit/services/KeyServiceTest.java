package epermit.services;


import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.JwsValidationResult;
import epermit.common.PermitProperties;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class KeyServiceTest {
    @Mock
    PermitProperties properties;

    @Mock
    KeyRepository repository;

    @Mock
    AuthorityRepository authorityRepository;

    @Test
    void keyShouldBeCreatedWhenSaltAndPasswordIsCorrect() {
        when(properties.getKeyPassword()).thenReturn("123456");
        KeyService service = new KeyService(properties, repository, authorityRepository);
        Key key = service.create("1");
        Assertions.assertNotNull(key.getSalt());
    }

    @Test
    void keyShouldNotBeCreatedWhenPasswordIsIncorrect() {
        when(properties.getKeyPassword()).thenReturn("123456");
        Assertions.assertThrows(IllegalStateException.class, () -> {
            KeyService service = new KeyService(properties, repository, authorityRepository);
            Key k = service.create("1");
            when(repository.findOneByEnabledTrue()).thenReturn(Optional.of(k));
            when(properties.getKeyPassword()).thenReturn("1234567");
            ECKey key = service.getKey();
            Assertions.assertNotNull(key);
        });
    }

    @Test
    @SneakyThrows
    void createJwsTest() {
        when(properties.getKeyPassword()).thenReturn("123456");
        when(properties.getIssuerCode()).thenReturn("UA");
        KeyService service = new KeyService(properties, repository, authorityRepository); 
        Key k = service.create("1");
        when(repository.findOneByEnabledTrue()).thenReturn(Optional.of(k));
        Authority authority = new Authority();
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setJwk(service.getKey().toPublicJWK().toJSONString());
        authorityKey.setKeyId("1");
        authority.addKey(authorityKey);
        when(authorityRepository.findOneByCode(anyString())).thenReturn(Optional.of(authority));    
        Map<String, String> claims = new HashMap<>();
        claims.put("issuer", "TR");
        claims.put("issued_for", "UA");
        String jws = service.createJws(claims);
        JwsValidationResult r = service.validateJws(jws);
        Assertions.assertTrue(r.isValid());
        JWSObject jwsObject = JWSObject.parse(jws);
        Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
        Assertions.assertEquals(payload.get("issuer"), "TR");
        System.out.println("--------------------------PAYLOAD-------------------------------------------------------");
        System.out.println(payload);
    }
}
