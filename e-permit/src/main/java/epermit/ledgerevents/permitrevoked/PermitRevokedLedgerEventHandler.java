package epermit.ledgerevents.permitrevoked;

import java.util.Optional;
import org.springframework.stereotype.Service;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPermitRepository permitRepository;

    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("PermitRevokedEventHandler started with {}", claims);
        PermitRevokedLedgerEvent event = (PermitRevokedLedgerEvent) claims;
        LedgerPermit permit = permitRepository.findOneByPermitId(event.getPermitId())
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND));
        if (permit.isUsed())
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        if (!permit.getIssuer().equals(event.getEventProducer()))
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        if (!permit.getIssuedFor().equals(event.getEventConsumer()))
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);

        permitRepository.delete(permit);
    }
}
