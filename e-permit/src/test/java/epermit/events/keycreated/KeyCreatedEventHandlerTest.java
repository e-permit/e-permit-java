package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)

public class KeyCreatedEventHandlerTest {
    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    KeyCreatedEventHandler handler;

    @Captor
    ArgumentCaptor<Authority> captor;

    @Test
    void saveKeyTest() {
        when(authorityRepository.findOneByCode("UA")).thenReturn(Optional.of(new Authority()));
        String jwk = "jwk";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        handler.handle(event);
        verify(authorityRepository).save(captor.capture());
        AuthorityKey authorityKey = captor.getValue().getKeys().get(0);
        assertEquals("1", authorityKey.getKeyId());
        assertEquals(jwk, authorityKey.getJwk());
        assertEquals(utc, authorityKey.getValidFrom());
    }

    @Test
    void disableOldKeyTest() {
        Authority authority = new Authority();
        AuthorityKey oldAuthorityKey = new AuthorityKey();
        authority.addKey(oldAuthorityKey);
        when(authorityRepository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        String jwk = "jwk";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        handler.handle(event);
        verify(authorityRepository).save(captor.capture());
        AuthorityKey authorityKey = captor.getValue().getKeys().get(0);
        assertNotNull(authorityKey.getValidUntil());
    }
}



/*
 * verify(authorityRepository, times(1)) .save(Mockito.argThat(new ArgumentMatcher<Authority>() {
 * 
 * @Override public boolean matches(Authority argument) { AuthorityKey key =
 * argument.getKeys().get(0); if (!key.getKeyId().equals("1")) return false; if
 * (!key.getJwk().equals("jwk")) return false; if (key.getValidFrom() != utc) return false; return
 * true; } }));
 */