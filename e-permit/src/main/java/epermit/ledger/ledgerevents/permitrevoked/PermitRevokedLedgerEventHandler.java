package epermit.ledger.ledgerevents.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPermit;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPermitRepository permitRepository;

    public void handle(Object e) {
        log.info("PermitRevokedEventHandler started with {}", e);
        PermitRevokedLedgerEvent event = (PermitRevokedLedgerEvent) e;
        LedgerPermit permit = permitRepository.findOneByPermitId(event.getPermitId()).get();
        permitRepository.delete(permit);
    }
}
