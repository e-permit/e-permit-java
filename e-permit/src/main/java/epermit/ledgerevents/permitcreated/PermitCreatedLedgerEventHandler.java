package epermit.ledgerevents.permitcreated;

import epermit.commons.Check;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.GsonUtil;
import epermit.utils.PermitUtil;
import java.util.Map;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitCreatedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;
    private final PermitUtil permitUtil;

    @SneakyThrows
    public LedgerEventHandleResult handle(Map<String, Object> claims) {
        log.info("PermitCreatedEventHandler started with {}", claims);
        PermitCreatedLedgerEvent event = GsonUtil.fromMap(claims, PermitCreatedLedgerEvent.class);
        Check.assertEquals(event.getPermitIssuer(), event.getEventIssuer(), "");
        Check.assertEquals(event.getPermitIssuer(), event.getEventIssuer(), "");
        validatePermitId(event);
        boolean exist = permitRepository.existsByPermitId(event.getPermitId());
        Check.assertTrue(exist, "PERMIT_EXIST");
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId(event.getCompanyId());
        permit.setCompanyName(event.getCompanyName());
        permit.setExpireAt(event.getExpireAt());
        permit.setIssuedAt(event.getIssuedAt());
        permit.setIssuer(event.getEventIssuer());
        permit.setIssuedFor(event.getEventIssuedFor());
        permit.setPermitId(event.getPermitId());
        permit.setPermitType(event.getPermitType());
        permit.setPermitYear(event.getPermitYear());
        permit.setPlateNumber(event.getPlateNumber());
        permit.setSerialNumber(event.getSerialNumber());
        if (event.getClaims() != null && !event.getClaims().isEmpty()) {
            permit.setClaims(GsonUtil.getGson().toJson(event.getClaims()));
        }
        log.info("PermitCreatedEventFactory ended with {}", permit);
        permitRepository.save(permit);
        return LedgerEventHandleResult.success();
    }

    public void validatePermitId(PermitCreatedLedgerEvent event) {
        CreatePermitIdInput input = new CreatePermitIdInput();
        input.setIssuedFor(event.getEventIssuedFor());
        input.setIssuer(event.getEventIssuer());
        input.setPermitType(event.getPermitType());
        input.setPermitYear(event.getPermitYear());
        input.setSerialNumber(event.getSerialNumber());
        String expectedPermitId = permitUtil.getPermitId(input);
        log.info("Expected permit id is {}", expectedPermitId);
        Check.assertEquals(expectedPermitId, event.getPermitId(), "INVALID_PERMITID");
    }
}



/*
 * Authority authority = authorityRepository.findOneByCode(issuer); Boolean r =
 * authority.getVerifierQuotas().stream() .anyMatch(x -> x.isActive() && x.getPermitType() ==
 * permitType && serialNumber >= x.getStartNumber() && serialNumber <= x.getEndNumber());
 * log.info("isQuotaSufficient ruslt is {}", r); return r;
 */
