package epermit.ledgerevents.permitrevoked;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPermitRepository permitRepository;

    @SneakyThrows
    public void handle(Map<String, Object> claims) {
        log.info("PermitRevokedEventHandler started with {}", claims);
        PermitRevokedLedgerEvent event = GsonUtil.fromMap(claims, PermitRevokedLedgerEvent.class);
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(event.getPermitId());
        Check.assertTrue(permitR.isPresent(), ErrorCodes.PERMIT_NOTFOUND);
        LedgerPermit permit = permitR.get();
        Check.assertEquals(permit.getIssuer(), event.getEventProducer(), ErrorCodes.PERMIT_NOTFOUND);
        Check.assertEquals(permit.getIssuedFor(), event.getEventConsumer(), ErrorCodes.PERMIT_NOTFOUND);
        permitRepository.delete(permit);
    }
}
