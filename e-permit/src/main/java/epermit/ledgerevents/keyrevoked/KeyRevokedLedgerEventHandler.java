package epermit.ledgerevents.keyrevoked;

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
@Service("KEY_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyRevokedLedgerEventHandler implements LedgerEventHandler {
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;

    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("KeyRevokedLedgerEvent started with {}", claims);
        KeyRevokedLedgerEvent e = (KeyRevokedLedgerEvent) claims;
        if (e.getAuthority().equals(properties.getIssuerCode())) {
            Authority authority = authorityRepository.findOneByCode(e.getAuthority()).orElseThrow();
            if(authority.getValidKeys().size() == 1){
                throw new EpermitValidationException(ErrorCodes.INSUFFICIENT_KEY);
            }

            AuthorityKey key = authority.getValidKeyById(e.getKeyId());
            key.setRevoked(true);
            key.setRevokedAt(e.getRevokedAt());
           
            log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
            authorityRepository.save(authority);
        }
    }
}
