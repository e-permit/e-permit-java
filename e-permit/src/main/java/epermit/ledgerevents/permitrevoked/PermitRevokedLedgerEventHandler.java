package epermit.ledgerevents.permitrevoked;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.commons.Check;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.ledgerevents.LedgerEventType;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPermitRepository permitRepository;

    @SneakyThrows
    public LedgerEventHandleResult handle(Map<String, Object> claims) {
        log.info("PermitRevokedEventHandler started with {}", claims);
        PermitRevokedLedgerEvent event = GsonUtil.fromMap(claims, PermitRevokedLedgerEvent.class);
        Check.assertEquals(event.getEventType(), LedgerEventType.PERMIT_REVOKED,
                "INVALID_EVENTTYPE");
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(event.getPermitId());
        Check.assertTrue(!permitR.isPresent(), "INVALID_PERMITID");
        LedgerPermit permit = permitR.get();
        Check.assertEquals(permit.getIssuedFor(), event.getEventIssuedFor(),
                "PERMIT_EVENT_MISMATCH");
        Check.assertEquals(permit.getIssuer(), event.getEventIssuer(), "PERMIT_EVENT_MISMATCH");
        permitRepository.delete(permit);
        return LedgerEventHandleResult.success();
    }
}