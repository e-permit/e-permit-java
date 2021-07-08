package epermit.ledgerevents.permitused;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerPermitActivity;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.GsonUtil;
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
    public LedgerEventHandleResult handle(Map<String, Object> claims) {
        log.info("PermitUsedEventHandler started with {}", claims);
        PermitUsedLedgerEvent event= GsonUtil.fromMap(claims, PermitUsedLedgerEvent.class);
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(event.getPermitId());
        if (!permitR.isPresent()) {
            log.info("PermitRevokedEventValidator result is INVALID_PERMITID");
            return LedgerEventHandleResult.fail("INVALID_PERMITID");
        }
        LedgerPermit permit = permitR.get();
        if (!(permit.getIssuedFor().equals(event.getEventIssuedFor())
                && permit.getIssuer().equals(event.getEventIssuer()))) {
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
