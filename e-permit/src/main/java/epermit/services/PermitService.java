package epermit.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.utils.PermitUtil;
import epermit.utils.QuotaUtil;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.CreatePermitIdDto;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.dtos.PermitListPageParams;
import epermit.models.dtos.PermitListParams;
import epermit.models.dtos.PermitActivityDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermitService {
    private final PermitUtil permitUtil;
    private final EPermitProperties properties;
    private final LedgerEventUtil ledgerEventUtil;
    private final LedgerQuotaRepository quotaRepository;
    private final ModelMapper modelMapper;
    private final LedgerPermitRepository permitRepository;

    private PermitDto mapPermit(LedgerPermit permit) {
        PermitDto dto = modelMapper.map(permit, PermitDto.class);
        List<PermitActivityDto> activityDtos = new ArrayList<>();
        permit.getActivities().forEach(act -> {
            PermitActivityDto actDto = new PermitActivityDto();
            actDto.setActivityType(act.getActivityType());
            actDto.setActivityDetails(act.getActivityDetails());
            LocalDateTime ts = LocalDateTime
                    .ofInstant(Instant.ofEpochSecond(act.getActivityTimestamp()), ZoneId.of("UTC"));
            actDto.setActivityTimestamp(ts.toString());
            activityDtos.add(actDto);
        });
        dto.setActivities(activityDtos);
        return dto;
    }

    public PermitDto getById(UUID id) {
        PermitDto dto = mapPermit(permitRepository.findById(id).get());
        return dto;
    }

    public Optional<PermitDto> getByPermitId(String id) {
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(id);
        if (permitR.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapPermit(permitR.get()));
    }

    public List<PermitListItem> getAll(PermitListParams input) {
        List<epermit.entities.LedgerPermit> entities = permitRepository.findAll(filterAllPermits(input));
        return entities.stream().map(x -> modelMapper.map(x, PermitListItem.class)).toList();
    }

    public Page<PermitListItem> getPage(PermitListPageParams input) {
        Page<epermit.entities.LedgerPermit> entities = permitRepository.findAll(filterPermits(input),
                PageRequest.of(input.getPage(), 10, Sort.by("createdAt").descending()));
        return entities.map(x -> modelMapper.map(x, PermitListItem.class));
    }

    @Transactional
    public CreatePermitResult createPermit(CreatePermitInput input) {
        log.info("Permit create command {}", input);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LedgerQuota quota = quotaRepository.findOne(QuotaUtil.filterQuotas(properties.getIssuerCode(),
                input.getIssuedFor(), input.getPermitType(), input.getPermitYear()))
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA));
        CreatePermitIdDto idInput = new CreatePermitIdDto();
        idInput.setIssuedFor(input.getIssuedFor());
        idInput.setIssuer(properties.getIssuerCode());
        idInput.setPermitType(input.getPermitType());
        idInput.setPermitYear(input.getPermitYear());
        idInput.setSerialNumber(quota.getNextSerial());
        String permitId = permitUtil.getPermitId(idInput);
        String issuer = properties.getIssuerCode();
        String issuedAt = LocalDateTime.now(ZoneOffset.UTC).format(dtf);
        String expireAt = "31/01/" + Integer.toString(input.getPermitYear() + 1);
        String prevEventId = ledgerEventUtil.getPreviousEventId(input.getIssuedFor());

        PermitCreatedLedgerEvent e = new PermitCreatedLedgerEvent(issuer, input.getIssuedFor(), prevEventId);
        e.setPermitId(permitId);
        e.setExpireAt(expireAt);
        e.setIssuedAt(issuedAt);
        e.setCompanyId(input.getCompanyId());
        e.setCompanyName(input.getCompanyName());
        e.setPermitType(input.getPermitType());
        e.setPermitYear(input.getPermitYear());
        e.setPlateNumber(input.getPlateNumber());
        e.setSerialNumber(quota.getNextSerial());
        e.setPermitIssuer(properties.getIssuerCode());
        e.setPermitIssuedFor(input.getIssuedFor());
        if (input != null && !input.getOtherClaims().isEmpty()) {
            e.setOtherClaims(input.getOtherClaims());
        }

        ledgerEventUtil.persistAndPublishEvent(e);
        log.info("Permit create finished permit id is {}", permitId);
        return CreatePermitResult.success(permitId);
    }

    @Transactional
    public void revokePermit(String permitId) {
        log.info("Revoke permit started {}", permitId);
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(permitId);
        if (permitR.isEmpty())
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        LedgerPermit permit = permitR.get();
        if (!permit.getIssuer().equals(properties.getIssuerCode()))
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        String prevEventId = ledgerEventUtil.getPreviousEventId(permit.getIssuedFor());
        PermitRevokedLedgerEvent e = new PermitRevokedLedgerEvent(properties.getIssuerCode(),
                permit.getIssuedFor(), prevEventId);
        e.setPermitId(permit.getPermitId());

        ledgerEventUtil.persistAndPublishEvent(e);
    }

    @Transactional
    public void permitUsed(String permitId, PermitUsedInput input) {
        log.info("Permit used started {}", input);
        LedgerPermit permit = permitRepository.findOneByPermitId(permitId)
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND));
        if (!permit.getIssuedFor().equals(properties.getIssuerCode()))
            throw new EpermitValidationException(ErrorCodes.PERMIT_NOTFOUND);
        String prevEventId = ledgerEventUtil.getPreviousEventId(permit.getIssuer());
        PermitUsedLedgerEvent e = new PermitUsedLedgerEvent(properties.getIssuerCode(),
                permit.getIssuer(), prevEventId);

        e.setActivityTimestamp(input.getActivityTimestamp());
        e.setActivityDetails(input.getActivityDetails());
        e.setPermitId(permitId);
        e.setActivityType(input.getActivityType());
        ledgerEventUtil.persistAndPublishEvent(e);

    }

    @SneakyThrows
    public byte[] generatePdf(String permitId) {
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(permitId);
        if (permitR.isPresent()) {
            return permitUtil.generatePdf(permitR.get());
        }
        throw new EpermitValidationException("Permit not found", ErrorCodes.PERMIT_NOTFOUND);
    }

    static Specification<LedgerPermit> filterAllPermits(PermitListParams input) {
        Specification<LedgerPermit> spec = (permit, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (input.getIssuer() != null) {
                predicates.add(cb.equal(permit.get("issuer"), input.getIssuer()));
            }
            if (input.getIssuedFor() != null) {
                predicates.add(cb.equal(permit.get("issuedFor"), input.getIssuedFor()));
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

    static Specification<LedgerPermit> filterPermits(PermitListPageParams input) {
        Specification<LedgerPermit> spec = (permit, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (input.getIssuer() != null) {
                predicates.add(cb.equal(permit.get("issuer"), input.getIssuer()));
            }
            if (input.getIssuedFor() != null) {
                predicates.add(cb.equal(permit.get("issuedFor"), input.getIssuedFor()));
            }
            if (input.getIssuedAt() != null) {
                predicates.add(cb.equal(permit.get("issuedAt"), input.getIssuedAt()));
            }
            if (input.getPermitType() != null) {
                predicates.add(cb.equal(permit.get("permitType"), input.getPermitType()));
            }
            if (input.getPermitYear() != null) {
                predicates.add(cb.equal(permit.get("permitYear"), input.getPermitYear()));
            }
            if (input.getCreatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime createdAtTime = LocalDateTime.parse(input.getCreatedAt(), formatter);
                predicates.add(cb.greaterThan(permit.get("createdAt"), createdAtTime));
            }
            // predicates.add(cb.asc(permit.get("serial_number")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }

}
