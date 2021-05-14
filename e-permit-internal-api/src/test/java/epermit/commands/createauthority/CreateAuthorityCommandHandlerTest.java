
package epermit.commands.createauthority;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import epermit.common.Authority;
import epermit.common.CommandResult;
import epermit.common.PublicJwk;
import epermit.common.PublicKey;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class CreateAuthorityCommandHandlerTest {
    @Mock
    AuthorityRepository repository;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    CreateAuthorityCommandHandler handler;

    @Test
    void handleTest() {
        PublicJwk jwk = new PublicJwk();
        jwk.setKid("1");
        PublicKey publicKey = new PublicKey();
        publicKey.setKeyId("1");
        publicKey.setValidFrom(Long.valueOf(1));
        publicKey.setJwk(jwk);
        Authority authority = new Authority();
        authority.setCode("code");
        authority.setVerifyUri("verifyUri");
        authority.setKeys(List.of(publicKey));
        when(restTemplate.getForEntity("apiUri", Authority.class))
                .thenReturn(ResponseEntity.ok(authority));
        CreateAuthorityCommand command = new CreateAuthorityCommand();
        command.setApiUri("apiUri");
        command.setCode("code");
        command.setName("name");
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
        epermit.entities.Authority a = (epermit.entities.Authority)r.getResult();
        assertEquals(1, a.getKeys().size());
    }
}


/*
 * @Captor ArgumentCaptor<epermit.entities.Authority> captor; verify(repository,
 * times(1)).save(captor.capture()); epermit.entities.Authority a = captor.getValue();
 * assertEquals("apiUri", a.getApiUri()); assertEquals("code", a.getCode()); assertEquals("name",
 * a.getName()); assertEquals(authorityMock.getVerifyUri(), a.getVerifyUri());
 */
