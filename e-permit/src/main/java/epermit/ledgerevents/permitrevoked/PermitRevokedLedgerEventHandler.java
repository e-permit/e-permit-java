package epermit.ledgerevents.permitrevoked;

import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPermitRepository permitRepository;

    public LedgerEventHandleResult handle(Object e) {
        log.info("PermitRevokedEventHandler started with {}", e);
        PermitRevokedLedgerEvent event = (PermitRevokedLedgerEvent) e;
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
        permitRepository.delete(permit);
        return LedgerEventHandleResult.success();
    }
}
