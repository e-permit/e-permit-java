package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.PublicJwk;
import epermit.models.dtos.PublicKey;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.services.AuthorityService;
import epermit.utils.GsonUtil;

@DataJpaTest
public class AuthorityKeyRepositoryIT {
    private String publicJwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";

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
        AuthorityService authorityService = new AuthorityService(authorityRepository, null, null, null, new ModelMapper(), null);
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
        //authorityRepository.save(authority);
        Optional<AuthorityKey> authorityKeyR = authorityKeyRepository.findOneByIssuerAndKeyId("UZ", "1");
        assertTrue(authorityKeyR.isPresent());
    }
    
}
