package epermit.services;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;
    private final LedgerEventUtil ledgerEventUtil;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;
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
}

