package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.QuotaDto;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;
    private final LedgerEventUtil ledgerEventUtil;
    private final LedgerQuotaRepository ledgerQuotaRepository;
    private final LedgerPermitRepository ledgerPermitRepository;
    private final ModelMapper modelMapper;

    public List<AuthorityDto> getAll() {
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        return all.stream().map(x -> modelMapper.map(x, AuthorityDto.class))
                .collect(Collectors.toList());
    }

    public AuthorityDto getByCode(String code) {
        Authority authority = authorityRepository.findOneByCode(code);
        AuthorityDto dto = modelMapper.map(authority, AuthorityDto.class);
        List<epermit.entities.LedgerQuota> quotaEntities = ledgerQuotaRepository.findAll();

        List<QuotaDto> quotas = quotaEntities.stream().filter(
                x -> x.getPermitIssuer().equals(code) || x.getPermitIssuedFor().equals(code))
                .map(x -> modelMapper.map(x, QuotaDto.class)).collect(Collectors.toList());
        quotas.forEach(quota -> {
            quota.setUsedCount(ledgerPermitRepository.count(filterUsedPermits(quota)));
        });
        dto.setQuotas(quotas);
        return dto;
    }

    @Transactional
    public void create(CreateAuthorityInput input, AuthorityConfig config) {
        log.info("Authority create command: {}", input);
        Authority exist = authorityRepository.findOneByCode(config.getCode());
        if (exist != null)
            throw new EpermitValidationException(ErrorCodes.AUTHORITY_ALREADY_EXISTS);

        Authority authority = new Authority();
        authority.setApiUri(input.getApiUri());
        authority.setCode(config.getCode());
        authority.setName(config.getName());

        log.info("Authority created: {}", authority);
        authorityRepository.save(authority);
    }

    @Transactional
    public void createQuota(CreateQuotaInput input) {
        log.info("Quota create command: {}", input);
        Authority authority = authorityRepository.findOneByCode(input.getAuthorityCode());
        if (authority == null)
            throw new EpermitValidationException(ErrorCodes.AUTHORITY_ALREADY_EXISTS);
        String issuer = properties.getIssuerCode();
        String prevEventId = ledgerEventUtil.getPreviousEventId(input.getAuthorityCode());
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent(issuer, input.getAuthorityCode(), prevEventId);
        event.setQuantity(input.getQuantity());
        event.setPermitType(input.getPermitType());
        event.setPermitYear(input.getPermitYear());
        event.setPermitIssuer(input.getAuthorityCode());
        event.setPermitIssuedFor(issuer);
        ledgerEventUtil.persistAndPublishEvent(event);
        log.info("Quota created: {}", event);
    }

    public static Specification<LedgerQuota> filterQuotas(String iss, String issFor, PermitType pType, Integer pYear) {
        Specification<LedgerQuota> spec = (quota, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(quota.get("permitIssuer"), iss));
            predicates.add(cb.equal(quota.get("permitIssuedFor"), issFor));
            predicates.add(cb.equal(quota.get("permitType"), pType));
            predicates.add(cb.equal(quota.get("permitYear"), pYear));
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}
