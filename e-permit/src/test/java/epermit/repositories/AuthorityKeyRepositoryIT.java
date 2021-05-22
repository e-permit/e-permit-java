package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;

@DataJpaTest
public class AuthorityKeyRepositoryIT {
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private AuthorityKeyRepository authorityKeyRepository;

    @Test
    void test(){
        Authority authority = new Authority();
        authority.setCode("TR");
        authority.setApiUri("apiUri");
        authority.setName("name");
        authority.setVerifyUri("verifyUri");
        authority.setCreatedAt(OffsetDateTime.now());
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setCreatedAt(OffsetDateTime.now());
        key.setJwk("jwk");
        key.setKeyId("1");
        key.setValidFrom(Long.valueOf(1));
        authority.addKey(key);
        authorityRepository.save(authority);
        Optional<AuthorityKey> authorityKeyR = authorityKeyRepository.findOneByIssuerAndKeyId("TR", "1");
        assertTrue(authorityKeyR.isPresent());
    }
    
}
