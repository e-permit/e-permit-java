package epermit.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.utils.PermitUtil;
import epermit.entities.IssuerQuotaSerialNumber;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PermitDto;
import epermit.models.enums.IssuerQuotaSerialNumberState;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.CreateQrCodeInput;
import epermit.models.inputs.PermitListInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.IssuerQuotaSerialNumberRepository;
import epermit.repositories.LedgerPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermitService {
    private final PermitUtil permitUtil;
    private final EPermitProperties properties;
    private final LedgerEventUtil ledgerEventUtil;
    private final ModelMapper modelMapper;
    private final LedgerPermitRepository permitRepository;
    private final IssuerQuotaSerialNumberRepository issuerQuotaSerialNumberRepository;

    public PermitDto getById(Long id) {
        PermitDto dto = modelMapper.map(permitRepository.findById(id).get(), PermitDto.class);
        return dto;
    }

    public Page<PermitDto> getAll(PermitListInput input) {
        Page<epermit.entities.LedgerPermit> entities =
                permitRepository.findAll(filterPermits(input), PageRequest.of(input.getPage(), 10));
        return entities.map(x -> modelMapper.map(x, PermitDto.class));
    }

    @Transactional
    public CreatePermitResult createPermit(CreatePermitInput input) {
        log.info("Permit create command {}", input);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        IssuerQuotaSerialNumber serialNumber =
                issuerQuotaSerialNumberRepository.getSerialNumber(properties.getIssuerCode(),
                        input.getIssuedFor(), input.getPermitType(), input.getPermitYear());
        CreatePermitIdInput idInput = new CreatePermitIdInput();
        idInput.setIssuedFor(input.getIssuedFor());
        idInput.setIssuer(properties.getIssuerCode());
        idInput.setPermitType(input.getPermitType());
        idInput.setPermitYear(input.getPermitYear());
        idInput.setSerialNumber(serialNumber.getSerialNumber());
        String permitId = permitUtil.getPermitId(idInput);
        String issuer = properties.getIssuerCode();
        String issuedAt = LocalDateTime.now(ZoneOffset.UTC).format(dtf);
        String expireAt = "30/01/" + Integer.toString(input.getPermitYear() + 1);
        String prevEventId = ledgerEventUtil.getPreviousEventId(input.getIssuedFor());
        CreateQrCodeInput qrCodeInput = new CreateQrCodeInput();
        qrCodeInput.setCompanyName(input.getCompanyName());
        qrCodeInput.setExpireAt(expireAt);
        qrCodeInput.setId(permitId);
        qrCodeInput.setIssuedAt(issuedAt);
        qrCodeInput.setPlateNumber(input.getPlateNumber());
        String qrCode = permitUtil.generateQrCode(qrCodeInput);
        PermitCreatedLedgerEvent e =
                new PermitCreatedLedgerEvent(issuer, input.getIssuedFor(), prevEventId);
        e.setPermitId(permitId);
        e.setExpireAt(expireAt);
        e.setIssuedAt(issuedAt);
        e.setCompanyId(input.getCompanyId());
        e.setCompanyName(input.getCompanyName());
        e.setPermitType(input.getPermitType());
        e.setPermitYear(input.getPermitYear());
        e.setPlateNumber(input.getPlateNumber());
        e.setSerialNumber(serialNumber.getSerialNumber());
        e.setPermitIssuer(properties.getIssuerCode());
        e.setPermitIssuedFor(input.getIssuedFor());
        if (!input.getOtherClaims().isEmpty()) {
            e.setOtherClaims(input.getOtherClaims());
        }
        serialNumber.setState(IssuerQuotaSerialNumberState.USED);
        issuerQuotaSerialNumberRepository.save(serialNumber);
        ledgerEventUtil.persistAndPublishEvent(e);
        log.info("Permit create finished permit id is {}", permitId);
        return CreatePermitResult.success(permitId, qrCode);
    }

    @Transactional
    public void revokePermit(Long id) {
        log.info("Revoke permit started {}", id);
        LedgerPermit permit = permitRepository.findById(id).get();
        String issuer = properties.getIssuerCode();
        String prevEventId = ledgerEventUtil.getPreviousEventId(permit.getIssuedFor());
        PermitRevokedLedgerEvent e =
                new PermitRevokedLedgerEvent(issuer, permit.getIssuedFor(), prevEventId);
        e.setPermitId(permit.getPermitId());
        // set revoked serial number
        ledgerEventUtil.persistAndPublishEvent(e);
    }

    @Transactional
    public void permitUsed(PermitUsedInput input) {
        log.info("Permit used started {}", input);
        LedgerPermit permit = permitRepository.findOneByPermitId(input.getPermitId()).get();
        String prevEventId = ledgerEventUtil.getPreviousEventId(permit.getIssuer());
        PermitUsedLedgerEvent e = new PermitUsedLedgerEvent(properties.getIssuerCode(),
                permit.getIssuer(), prevEventId);
        e.setPermitId(permit.getPermitId());
        e.setActivityTimestamp(input.getActivityTimestamp());
        e.setActivityDetails(input.getActivityDetails());
        e.setPermitId(input.getPermitId());
        e.setActivityType(input.getActivityType());
        ledgerEventUtil.persistAndPublishEvent(e);

    }

    static Specification<LedgerPermit> filterPermits(PermitListInput input) {
        Specification<LedgerPermit> spec = (permit, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (input.getIssuer() != null) {
                predicates.add(cb.equal(permit.get("issuer"), input.getIssuer()));
            }
            if (input.getPermitType() != null) {
                predicates.add(cb.equal(permit.get("permitType"), input.getPermitType()));
            }
            if (input.getPermitYear() != null) {
                predicates.add(cb.equal(permit.get("permitYear"), input.getPermitYear()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }

}
