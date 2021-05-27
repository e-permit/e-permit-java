package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;

@DataJpaTest
public class AuthorityKeyRepositoryIT {
    private String publicJwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    void saveTest(){
        Authority authority = new Authority();
        authority.setCode("TR");
        authority.setApiUri("apiUri");
        authority.setName("name");
        authority.setVerifyUri("verifyUri");
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setJwk("jwk");
        key.setKeyId("1");
        authority.addKey(key);
        authorityRepository.save(authority);
        Example<Authority> example = Example.of(authority);
        assertTrue(authorityRepository.exists(example));
        //assertEquals(null, authority.getCreatedAt());
    }
    
}


/**
 * 
 * AuthorityService authorityService = new AuthorityService(authorityRepository, null, null, null, new ModelMapper(), null);
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

         log.info("Private JWk ----------------------------------------------------");
        log.info(keyR.get().getPrivateJwk());
        log.info(keyR.get().getSalt());
        IssuedPermit permit = new IssuedPermit();
        permit.setPermitId("TR-UZ-2021-1-1");
        permit.setIssuedAt("3/6/2021");
        permit.setExpireAt("31/1/2022");
        permit.setPlateNumber("06AA2021");
        permit.setCompanyName("ABC Limited");
        String qrCode = permitUtil.generateQrCode(permit);
        log.info(qrCode);
 */