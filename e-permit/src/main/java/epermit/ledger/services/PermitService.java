package epermit.ledger.services;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.ledger.utils.PermitUtil;
import epermit.ledger.ledgerevents.LedgerEventUtil;
import epermit.ledger.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledger.ledgerevents.permitcreated.PermitCreatedLedgerEventHandler;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.models.inputs.CreatePermitIdInput;
import epermit.ledger.models.inputs.CreatePermitInput;
import epermit.ledger.models.results.CreatePermitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermitService {
    private final PermitUtil permitUtil;
    private final EPermitProperties properties;
    private final LedgerEventUtil ledgerEventUtil;

    @Transactional
    public CreatePermitResult createPermit(CreatePermitInput input) {
        log.info("Permit create command {}", input);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
     
        Optional<Integer> serialNumberResult = permitUtil.generateSerialNumber(
                input.getIssuedFor(), input.getPermitYear(), input.getPermitType());
        if (!serialNumberResult.isPresent()) {
            return CreatePermitResult.fail("INSUFFICIENT_QUOTA");
        }
        String permitId = permitUtil.getPermitId(new CreatePermitIdInput());
        String issuer = properties.getIssuerCode();
        String issuedAt = "";
        String expireAt = "";
        PermitCreatedLedgerEvent e = new PermitCreatedLedgerEvent();
        e.setCompanyId(input.getCompanyId());
        e.setIssuedFor(input.getIssuedFor());
        ledgerEventUtil.persistAndPublishEvent(e);
        log.info("Permit create finished permit id is {}", permitId);
        return CreatePermitResult.success(permitId);
    }

    @Transactional
    public void revokePermit(Long id) {
        log.info("Revoke permit started {}", id);
        
    }

    @Transactional
    public void permitUsed(Long id) {
        log.info("Permit used started {}", id);
        
    }

}


