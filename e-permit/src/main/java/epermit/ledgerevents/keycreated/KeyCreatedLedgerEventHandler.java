package epermit.ledgerevents.keycreated;

import org.springframework.stereotype.Service;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedLedgerEventHandler implements LedgerEventHandler {
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;

    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("KeyCreatedEventHandler started with {}", claims);
        KeyCreatedLedgerEvent e = (KeyCreatedLedgerEvent) claims;
        // Key events should only be handled by consumer
        if (e.getAuthority().equals(properties.getIssuerCode())) {
            Authority authority = authorityRepository.findOneByCode(e.getAuthority())
                    .orElseThrow(() -> new EpermitValidationException(ErrorCodes.AUTHORITY_NOT_FOUND));
            boolean keyExist = authority.getKeys().stream()
                    .anyMatch((k) -> k.getKeyId().equals(e.getKid()));
            if (keyExist) {
                throw new EpermitValidationException(ErrorCodes.KEYID_ALREADY_EXISTS);
            }
           
            AuthorityKey key = new AuthorityKey();
            key.setKeyId(e.getKid());
            key.setJwk(e.getJwk());
            authority.addKey(key);
            log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
            authorityRepository.save(authority);
        }

    }
}
