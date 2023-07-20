package epermit.ledgerevents.permitused;

import org.springframework.stereotype.Service;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerPermitActivity;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_USED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitUsedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;

    @SneakyThrows
    @Override
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("PermitUsedEventHandler started with {}", claims);
        PermitUsedLedgerEvent event = (PermitUsedLedgerEvent) claims;
        LedgerPermit permit = permitRepository.findOneByPermitId(event.getPermitId())
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND));
        if (!permit.getIssuer().equals(event.getEventConsumer()))
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        if (!permit.getIssuedFor().equals(event.getEventProducer()))
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        if (permit.isLocked())
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);

        permit.setUsed(true);
        LedgerPermitActivity activity = new LedgerPermitActivity();
        activity.setActivityType(event.getActivityType());
        activity.setActivityTimestamp(event.getActivityTimestamp());
        permit.addActivity(activity);
        log.info("PermitUsedEventHandler ended with {}", activity);
        permitRepository.save(permit);
    }
}
