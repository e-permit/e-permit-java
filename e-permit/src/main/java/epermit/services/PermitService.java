package epermit.services;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.utils.PermitUtil;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventType;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitListInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
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

        Optional<Integer> serialNumberResult = permitUtil.generateSerialNumber(input.getIssuedFor(),
                input.getPermitYear(), input.getPermitType());
        if (!serialNumberResult.isPresent()) {
            return CreatePermitResult.fail("INSUFFICIENT_QUOTA");
        }
        String permitId = permitUtil.getPermitId(new CreatePermitIdInput());
        String issuer = properties.getIssuerCode();
        String issuedAt = "";
        String expireAt = "";
        PermitCreatedLedgerEvent e = (PermitCreatedLedgerEvent) ledgerEventUtil
                .createLedgerEvent(LedgerEventType.PERMIT_CREATED, input.getIssuedFor());
        e.setCompanyId(input.getCompanyId());
        ledgerEventUtil.persistAndPublishEvent(e);
        log.info("Permit create finished permit id is {}", permitId);
        return CreatePermitResult.success(permitId);
    }

    @Transactional
    public void revokePermit(Long id) {
        log.info("Revoke permit started {}", id);

    }

    @Transactional
    public void permitUsed(String id, PermitUsedInput input) {
        log.info("Permit used started {}", id);

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


