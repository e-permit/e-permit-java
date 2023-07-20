package epermit.ledgerevents.keycreated;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.dtos.PublicJwk;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository keyRepository;
    private final ModelMapper modelMapper;

    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("KeyCreatedEventHandler started with {}", claims);
        KeyCreatedLedgerEvent e = (KeyCreatedLedgerEvent) claims;
        boolean keyExist = keyRepository.existsByAuthorityCodeAndKeyId(e.getEventProducer(), e.getKid());
        if (keyExist)
            throw new EpermitValidationException(ErrorCodes.KEYID_ALREADY_EXISTS);
        LedgerPublicKey key = new LedgerPublicKey();
        key.setKeyId(e.getKid());
        key.setAuthorityCode(e.getEventProducer());
        key.setJwk(GsonUtil.getGson().toJson(modelMapper.map(e, PublicJwk.class)));
        log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
        keyRepository.save(key);
    }
}
