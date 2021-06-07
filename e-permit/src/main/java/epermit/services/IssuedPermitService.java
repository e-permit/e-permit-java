package epermit.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.IssuedPermit;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.models.EPermitProperties;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.IssuedPermitListInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.IssuedPermitRepository;
import epermit.utils.PermitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssuedPermitService {
    private final ModelMapper modelMapper;
    private final PermitUtil permitUtil;
    private final EPermitProperties properties;
    private final IssuedPermitRepository issuedPermitRepository;
    private final PermitCreatedEventFactory permitCreatedEventFactory;
    private final PermitRevokedEventFactory permitRevokedEventFactory;

    public IssuedPermitDto getById(Long id) {
        IssuedPermitDto dto =
                modelMapper.map(issuedPermitRepository.findById(id).get(), IssuedPermitDto.class);
        return dto;
    }

    public Page<IssuedPermitDto> getAll(IssuedPermitListInput input) {
        Page<epermit.entities.IssuedPermit> entities = issuedPermitRepository
                .findAll(filterPermits(input), PageRequest.of(input.getPage(), 10));
        return entities.map(x -> modelMapper.map(x, IssuedPermitDto.class));
    }

    @Transactional
    public CreatePermitResult createPermit(CreatePermitInput input) {
        log.info("Permit create started");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String issuer = properties.getIssuerCode();
        Optional<Integer> serialNumberResult = permitUtil.generateSerialNumber(
                input.getIssuedFor(), input.getPermitYear(), input.getPermitType());
        if (!serialNumberResult.isPresent()) {
            return CreatePermitResult.fail("INSUFFICIENT_QUOTA");
        }
        String permitId = permitUtil.getPermitId(issuer, input.getIssuedFor(),
                input.getPermitType(), input.getPermitYear(), serialNumberResult.get());
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName(input.getCompanyName());
        permit.setIssuedFor(input.getIssuedFor());
        permit.setPermitId(permitId);
        permit.setPermitType(input.getPermitType());
        permit.setPermitYear(input.getPermitYear());
        permit.setPlateNumber(input.getPlateNumber());
        permit.setSerialNumber(serialNumberResult.get());
        permit.setIssuedAt(LocalDateTime.now(ZoneOffset.UTC).format(dtf));
        permit.setExpireAt("30/01/" + Integer.toString(input.getPermitYear() + 1));
        permit.setQrCode(permitUtil.generateQrCode(permit));
        issuedPermitRepository.save(permit);
        permitCreatedEventFactory.create(permit);
        log.info("Permit create finished permit id is {}", permit.getPermitId());
        return CreatePermitResult.success(permit.getPermitId());
    }

    @Transactional
    public void revokePermit(Long id) {
        Optional<IssuedPermit> permitOptional = issuedPermitRepository.findById(id);
        if (!permitOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PERMIT_NOTFOUND");
        }
        IssuedPermit permit = permitOptional.get();
        permit.setRevoked(true);
        permit.setRevokedAt(LocalDateTime.now());
        issuedPermitRepository.save(permit);
        permitRevokedEventFactory.create(permit.getIssuedFor(), permit.getPermitId());
    }

    static Specification<IssuedPermit> filterPermits(IssuedPermitListInput input) {
        Specification<IssuedPermit> spec = (permit, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            if (input.getIssuedFor() != null) {
                predicates.add(cb.equal(permit.get("issuedFor"), input.getIssuedFor()));
            }
            if (input.getPermitType() != null) {
                predicates.add(cb.equal(permit.get("permitType"), input.getPermitType()));
            }
            if (input.getPermitYear() != null) {
                predicates.add(cb.equal(permit.get("permitYear"), input.getPermitYear()));
            }
            if (input.getCompanyId() != null) {
                predicates.add(cb.equal(permit.get("companyId"), input.getCompanyId()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }

}

