package epermit.ledger.ledgerevents.permitcreated;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.ledger.ledgerevents.LedgerEventValidationResult;
import epermit.ledger.ledgerevents.LedgerEventValidator;
import epermit.ledger.models.inputs.CreatePermitIdInput;
import epermit.ledger.repositories.LedgerPermitRepository;
import epermit.ledger.utils.GsonUtil;
import epermit.ledger.utils.PermitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class PermitCreatedLedgerEventValidator implements LedgerEventValidator {
    private final LedgerPermitRepository permitRepository;
    private final PermitUtil permitUtil;

    public LedgerEventValidationResult validate(Map<String, Object> payload) {
        log.info("PermitCreatedEventValidator started with {}", payload);
        PermitCreatedLedgerEvent event = GsonUtil.fromMap(payload, PermitCreatedLedgerEvent.class);
        CreatePermitIdInput input = new CreatePermitIdInput();
        input.setIssuedFor(event.getIssuedFor());
        input.setIssuer(event.getIssuer());
        input.setPermitType(event.getPermitType().getCode());
        input.setPermitYear(Integer.toString(event.getPermitYear()));
        input.setSerialNumber(Integer.toString(event.getSerialNumber()));
        String expectedPermitId = permitUtil.getPermitId(input);
        if (!expectedPermitId.equals(event.getPermitId())) {
            log.info("PermitCreatedEventValidator result is INVALID_PERMITID");
            return LedgerEventValidationResult.fail("INVALID_PERMITID", event);
        }
        boolean exist = permitRepository.existsByPermitId(event.getPermitId());
        if (exist) {
            log.info("PermitCreatedEventValidator result is PERMIT_EXIST");
            return LedgerEventValidationResult.fail("PERMIT_EXIST", event);
        }

        if (!permitUtil.isQuotaSufficient(event.getIssuer(), event.getPermitYear(),
                event.getSerialNumber(), event.getPermitType())) {
            log.info("PermitCreatedEventValidator result is INSUFFICIENT_QUOTA");
            return LedgerEventValidationResult.fail("INSUFFICIENT_QUOTA", event);
        }
        log.info("PermitCreatedEventValidator is succeed");
        return LedgerEventValidationResult.success(event);
    }
}
