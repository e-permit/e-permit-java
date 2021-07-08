package epermit.ledgerevents.permitrevoked;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
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
        permitRepository.delete(permit);
        return LedgerEventHandleResult.success();
    }
}
