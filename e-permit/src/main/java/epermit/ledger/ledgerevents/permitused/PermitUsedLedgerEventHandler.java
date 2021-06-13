package epermit.ledger.ledgerevents.permitused;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPermit;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.models.valueobjects.LedgerPermitActivity;
import epermit.ledger.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_USED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitUsedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;

    @Override
    public void handle(Object e) {
        log.info("PermitUsedEventHandler started with {}", e);
        PermitUsedLedgerEvent event = (PermitUsedLedgerEvent) e;
        LedgerPermit permit = permitRepository.findOneByPermitId(event.getPermitId()).get();
        permit.setUsed(true);
        LedgerPermitActivity activity = new LedgerPermitActivity();
        activity.setActivityType(event.getActivityType());
        activity.setActivityTimestamp(event.getActivityTimestamp());
        permit.getActivities().add(activity);
        log.info("PermitUsedEventHandler ended with {}", activity);
        permitRepository.save(permit);
    }
}
