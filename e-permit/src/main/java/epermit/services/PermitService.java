package epermit.services;

import java.io.ByteArrayInputStream;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.github.dockerjava.api.exception.UnauthorizedException;
import epermit.utils.JwsUtil;
import epermit.utils.PermitUtil;
import epermit.commons.Check;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.SerialNumber;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.CreatePermitIdDto;
import epermit.models.dtos.CreateQrCodeDto;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.dtos.PermitListParams;
import epermit.models.dtos.PermitLockedDto;
import epermit.models.dtos.PermitActivityDto;
import epermit.models.enums.SerialNumberState;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.SerialNumberRepository;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
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
    private final JwsUtil jwsUtil;
    private final ModelMapper modelMapper;
    private final AuthorityRepository authorityRepository;
    private final LedgerPermitRepository permitRepository;
    private final SerialNumberRepository serialNumberRepository;
    private final RestTemplate restTemplate;

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


    public Page<PermitListItem> getAll(PermitListParams input) {
        Page<epermit.entities.LedgerPermit> entities =
                permitRepository.findAll(filterPermits(input),
                        PageRequest.of(input.getPage(), 10, Sort.by("serialNumber").descending()));
        return entities.map(x -> modelMapper.map(x, PermitListItem.class));
    }

    @Transactional
    public CreatePermitResult createPermit(CreatePermitInput input) {
        log.info("Permit create command {}", input);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        PageRequest pageable = PageRequest.of(0, 1, Sort.by(Direction.ASC, "serialNumber"));
        List<SerialNumber> serialNumbers =
                serialNumberRepository.findAll(filterSerialNumbers(input.getIssuedFor(),
                        input.getPermitYear(), input.getPermitType()), pageable).toList();
        Check.assertFalse(serialNumbers.isEmpty(), ErrorCodes.INSUFFICIENT_PERMIT_QUOTA);
        SerialNumber serialNumber = serialNumbers.get(0);
        CreatePermitIdDto idInput = new CreatePermitIdDto();
        idInput.setIssuedFor(input.getIssuedFor());
        idInput.setIssuer(properties.getIssuerCode());
        idInput.setPermitType(input.getPermitType());
        idInput.setPermitYear(input.getPermitYear());
        idInput.setSerialNumber(serialNumber.getSerialNumber());
        String permitId = permitUtil.getPermitId(idInput);
        String issuer = properties.getIssuerCode();
        String issuedAt = LocalDateTime.now(ZoneOffset.UTC).format(dtf);
        String expireAt = "31/01/" + Integer.toString(input.getPermitYear() + 1);
        String prevEventId = ledgerEventUtil.getPreviousEventId(input.getIssuedFor());
        CreateQrCodeDto qrCodeInput = new CreateQrCodeDto();
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
        e.setQrCode(qrCode);
        if (input != null && !input.getOtherClaims().isEmpty()) {
            e.setOtherClaims(input.getOtherClaims());
        }
        serialNumber.setState(SerialNumberState.USED);
        serialNumberRepository.save(serialNumber);
        ledgerEventUtil.persistAndPublishEvent(e);
        log.info("Permit create finished permit id is {}", permitId);
        return CreatePermitResult.success(permitId, qrCode);
    }

    public void revokePermit(String permitId) {
        log.info("Revoke permit started {}", permitId);
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(permitId);
        Check.assertTrue(permitR.isPresent(), ErrorCodes.PERMIT_NOTFOUND);
        LedgerPermit permit = permitR.get();
        Check.assertEquals(permit.getIssuer(), properties.getIssuerCode(),
                ErrorCodes.PERMIT_NOTFOUND);
        tryLockPermit(permit);
        commitRevokePermit(permit);
    }

    @Transactional
    public void permitUsed(String permitId, PermitUsedInput input) {
        log.info("Permit used started {}", input);
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(permitId);
        Check.assertTrue(permitR.isPresent(), ErrorCodes.PERMIT_NOTFOUND);
        LedgerPermit permit = permitR.get();
        Check.assertEquals(permit.getIssuedFor(), properties.getIssuerCode(),
                ErrorCodes.PERMIT_NOTFOUND);
        Check.assertFalse(permit.isLocked(), ErrorCodes.PERMIT_NOTFOUND);
        String prevEventId = ledgerEventUtil.getPreviousEventId(permit.getIssuer());
        PermitUsedLedgerEvent e = new PermitUsedLedgerEvent(properties.getIssuerCode(),
                permit.getIssuer(), prevEventId);
        e.setPermitId(permit.getPermitId());
        e.setActivityTimestamp(input.getActivityTimestamp());
        e.setActivityDetails(input.getActivityDetails());
        e.setPermitId(permitId);
        e.setActivityType(input.getActivityType());
        ledgerEventUtil.persistAndPublishEvent(e);

    }

    @Transactional
    public void handlePermitLocked(String jws) {
        if (!jwsUtil.validateJws(jws)) {
            throw new UnauthorizedException("Invalid jws");
        }
        String permitId = jwsUtil.getClaim(jws, "permit_id");
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(permitId);
        Check.assertTrue(permitR.isPresent(), ErrorCodes.PERMIT_NOTFOUND);
        LedgerPermit permit = permitR.get();
        Check.assertEquals(permit.getIssuedFor(), properties.getIssuerCode(),
                ErrorCodes.PERMIT_NOTFOUND);
        permit.setLocked(true);
        permitRepository.save(permit);
    }

    @SneakyThrows
    public byte[] generatePdf(String permitId) {
        Optional<LedgerPermit> permitR = permitRepository.findOneByPermitId(permitId);
        if(permitR.isPresent()){
            return permitUtil.generatePdf(permitR.get());
        }
        throw new EpermitValidationException("Permit not found", ErrorCodes.PERMIT_NOTFOUND);
    }

    boolean tryLockPermit(LedgerPermit permit) {
        PermitLockedDto lockedDto = new PermitLockedDto();
        lockedDto.setEventConsumer(permit.getIssuedFor());
        lockedDto.setEventProducer(permit.getIssuer());
        lockedDto.setEventTimestamp(Instant.now().getEpochSecond());
        String jws = jwsUtil.createJws(lockedDto);
        Authority authority = authorityRepository.findOneByCode(lockedDto.getEventConsumer());
        String url = authority.getApiUri() + "/events/permit-locked";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jws, headers);
        ResponseEntity<?> result = restTemplate.postForEntity(url, request, Void.class);
        if (result.getStatusCode() != HttpStatus.OK) {
            return false;
        }
        return true;
    }

    @Transactional
    void commitRevokePermit(LedgerPermit permit) {
        String prevEventId = ledgerEventUtil.getPreviousEventId(permit.getIssuedFor());
        PermitRevokedLedgerEvent e = new PermitRevokedLedgerEvent(properties.getIssuerCode(),
                permit.getIssuedFor(), prevEventId);
        e.setPermitId(permit.getPermitId());

        SerialNumber serialNumber = serialNumberRepository
                .findOne(filterSerialNumber(permit.getIssuedFor(), permit.getPermitYear(),
                        permit.getPermitType(), permit.getSerialNumber()))
                .get();
        serialNumber.setState(SerialNumberState.REVOKED);
        serialNumberRepository.save(serialNumber);
        ledgerEventUtil.persistAndPublishEvent(e);
    }

    static Specification<LedgerPermit> filterPermits(PermitListParams input) {
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

    static Specification<SerialNumber> filterSerialNumbers(String authority, int py,
            PermitType pt) {
        Specification<SerialNumber> spec = (sn, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(sn.get("authorityCode"), authority));
            predicates.add(cb.equal(sn.get("permitType"), pt));
            predicates.add(cb.equal(sn.get("permitYear"), py));
            Predicate p1 = cb.equal(sn.get("state"), SerialNumberState.CREATED);
            Predicate p2 = cb.equal(sn.get("state"), SerialNumberState.REVOKED);
            predicates.add(cb.or(p1, p2));
            Predicate p = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            return p;
        };
        return spec;
    }

    static Specification<SerialNumber> filterSerialNumber(String authority, int py, PermitType pt,
            int num) {
        Specification<SerialNumber> spec = (sn, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(sn.get("authorityCode"), authority));
            predicates.add(cb.equal(sn.get("permitType"), pt));
            predicates.add(cb.equal(sn.get("permitYear"), py));
            predicates.add(cb.equal(sn.get("serialNumber"), num));
            Predicate p = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            return p;
        };
        return spec;
    }

}
