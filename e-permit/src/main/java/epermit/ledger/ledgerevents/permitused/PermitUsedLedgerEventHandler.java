package epermit.ledger.ledgerevents.permitused;

import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPermit;
import epermit.ledger.entities.LedgerPermitActivity;
import epermit.ledger.ledgerevents.LedgerEventHandleResult;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_USED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitUsedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;

    @Override
    public LedgerEventHandleResult handle(Object e) {
        log.info("PermitUsedEventHandler started with {}", e);
        PermitUsedLedgerEvent event = (PermitUsedLedgerEvent) e;
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(event.getPermitId());
        if (!permitR.isPresent()) {
            log.info("PermitRevokedEventValidator result is INVALID_PERMITID");
            return LedgerEventHandleResult.fail("INVALID_PERMITID");
        }
        LedgerPermit permit = permitR.get();
        if (!(permit.getIssuedFor().equals(event.getIssuedFor())
                && permit.getIssuer().equals(event.getIssuer()))) {
            log.info("PermitRevokedEventValidator result is PERMIT_EVENT_MISMATCH");
            return LedgerEventHandleResult.fail("PERMIT_EVENT_MISMATCH");
        }
        permit.setUsed(true);
        LedgerPermitActivity activity = new LedgerPermitActivity();
        activity.setActivityType(event.getActivityType());
        activity.setActivityTimestamp(event.getActivityTimestamp());
        permit.addActivity(activity);
        log.info("PermitUsedEventHandler ended with {}", activity);
        permitRepository.save(permit);
        return LedgerEventHandleResult.success();
    }
}
