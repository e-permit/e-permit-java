package epermit.ledgerevents.permitused;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerPermitActivity;
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
    public void handle(Map<String, Object> claims) {
        log.info("PermitUsedEventHandler started with {}", claims);
        PermitUsedLedgerEvent event = GsonUtil.fromMap(claims, PermitUsedLedgerEvent.class);
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(event.getPermitId());
        Check.assertTrue(permitR.isPresent(), ErrorCodes.PERMIT_NOTFOUND);
        LedgerPermit permit = permitR.get();
        Check.assertEquals(permit.getIssuer(), event.getEventConsumer(), ErrorCodes.PERMIT_NOTFOUND);
        Check.assertEquals(permit.getIssuedFor(), event.getEventProducer(), ErrorCodes.PERMIT_NOTFOUND);
        permit.setUsed(true);
        LedgerPermitActivity activity = new LedgerPermitActivity();
        activity.setActivityType(event.getActivityType());
        activity.setActivityTimestamp(event.getActivityTimestamp());
        permit.addActivity(activity);
        log.info("PermitUsedEventHandler ended with {}", activity);
        permitRepository.save(permit);
    }
}
