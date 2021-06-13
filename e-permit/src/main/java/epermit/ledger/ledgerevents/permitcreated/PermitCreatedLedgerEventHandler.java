package epermit.ledger.ledgerevents.permitcreated;

import epermit.ledger.entities.LedgerPermit;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.LedgerPermitRepository;
import epermit.ledger.utils.GsonUtil;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitCreatedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;

    public void handle(Object e) {
        log.info("PermitCreatedEventHandler started with {}", e);
        PermitCreatedLedgerEvent event = (PermitCreatedLedgerEvent) e;
        Gson gson = GsonUtil.getGson();
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId(event.getCompanyId());
        permit.setCompanyName(event.getCompanyName());
        permit.setExpireAt(event.getExpireAt());
        permit.setIssuedAt(event.getIssuedAt());
        permit.setIssuer(event.getIssuer());
        permit.setIssuedFor(event.getIssuedFor());
        permit.setPermitId(event.getPermitId());
        permit.setPermitType(event.getPermitType());
        permit.setPermitYear(event.getPermitYear());
        permit.setPlateNumber(event.getPlateNumber());
        permit.setSerialNumber(event.getSerialNumber());
        if (event.getClaims() != null && !event.getClaims().isEmpty()) {
            permit.setClaims(gson.toJson(event.getClaims()));
        }
        log.info("PermitCreatedEventFactory ended with {}", permit);
        permitRepository.save(permit);
    }
}
