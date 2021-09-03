package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.appevents.QuotaCreated;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.SerialNumber;
import epermit.entities.LedgerPublicKey;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.enums.SerialNumberState;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.SerialNumberRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;
    private final LedgerEventUtil ledgerEventUtil;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;
    private final LedgerQuotaRepository ledgerQuotaRepository;
    private final SerialNumberRepository serialNumberRepository;
    private final ModelMapper modelMapper;

    private AuthorityDto entityToDto(Authority authority) {
        AuthorityDto dto = modelMapper.map(authority, AuthorityDto.class);
        return dto;
    }

    public List<AuthorityDto> getAll() {
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        return all.stream().map(x -> entityToDto(x)).collect(Collectors.toList());
    }

    public AuthorityDto getByCode(String code) {
        Authority authority = authorityRepository.findOneByCode(code);
        return entityToDto(authority);
    }

    @Transactional
    public void create(CreateAuthorityInput input, AuthorityConfig config) {
        log.info("Authority create command: {}", input);
        Authority authority = new Authority();
        authority.setApiUri(input.getApiUri());
        authority.setCode(config.getCode());
        authority.setName(config.getName());
        config.getKeys().forEach(k -> {
            LedgerPublicKey publicKey = new LedgerPublicKey();
            publicKey.setJwk(GsonUtil.getGson().toJson(k));
            publicKey.setKeyId(k.getKid());
            publicKey.setAuthorityCode(config.getCode());
            ledgerPublicKeyRepository.save(publicKey);
        });
        log.info("Authority created: {}", authority);
        authorityRepository.save(authority);
    }

    @Transactional
    public void createQuota(CreateQuotaInput input) {
        log.info("Quota create command: {}", input);
        String issuer = properties.getIssuerCode();
        String prevEventId = ledgerEventUtil.getPreviousEventId(input.getAuthorityCode());
        QuotaCreatedLedgerEvent event =
                new QuotaCreatedLedgerEvent(issuer, input.getAuthorityCode(), prevEventId);
        event.setEndNumber(input.getEndNumber());
        event.setStartNumber(input.getStartNumber());
        event.setPermitType(input.getPermitType());
        event.setPermitYear(input.getPermitYear());
        event.setPermitIssuer(input.getAuthorityCode());
        event.setPermitIssuedFor(issuer);
        log.info("Quota created: {}", event);
        ledgerEventUtil.persistAndPublishEvent(event);
    }

    @Transactional
    @SneakyThrows
    public void handleReceivedQuota(QuotaCreated e) {
        Optional<LedgerQuota> r = ledgerQuotaRepository.findOne(filterEvents(e));
        if (r.isPresent()) {
            LedgerQuota lq = r.get();
            log.info("Ledger quota found {}", lq.getPermitIssuedFor());
            List<SerialNumber> serialNumbers = new ArrayList<>();
            for (int i = lq.getStartNumber(); i <= lq.getEndNumber(); i++) {
                SerialNumber s = new SerialNumber();
                s.setAuthorityCode(lq.getPermitIssuedFor());
                s.setPermitType(lq.getPermitType());
                s.setPermitYear(lq.getPermitYear());
                s.setSerialNumber(i);
                s.setState(SerialNumberState.CREATED);
                serialNumbers.add(s);
            }
            serialNumberRepository.saveAll(serialNumbers);
        } else {
            log.error("Ledger quota not found");
            throw new Exception("Ledger quota not found");
        }
    }

    static Specification<LedgerQuota> filterEvents(QuotaCreated e) {
        Specification<LedgerQuota> spec = (q, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(q.get("permitIssuedFor"), e.getPermitIssuedFor()));
            predicates.add(cb.equal(q.get("startNumber"), e.getStartNumber()));
            predicates.add(cb.equal(q.get("endNumber"), e.getEndNumber()));
            predicates.add(cb.equal(q.get("permitYear"), e.getPermitYear()));
            predicates.add(cb.equal(q.get("permitType"), e.getPermitType()));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}

