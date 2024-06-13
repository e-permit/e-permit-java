package epermit.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.AuthorityListItem;
import epermit.models.dtos.QuotaDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerQuotaRepository;
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
    private final ModelMapper modelMapper;

    public List<AuthorityListItem> getAll() {
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        return all.stream().map(x -> modelMapper.map(x, AuthorityListItem.class))
                .collect(Collectors.toList());
    }

    public AuthorityDto getByCode(String code) {
        List<epermit.entities.LedgerQuota> quotaEntities = ledgerQuotaRepository.findAll();
        Authority authority = authorityRepository.findOneByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        AuthorityDto dto = modelMapper.map(authority, AuthorityDto.class);
        List<QuotaDto> quotas = quotaEntities.stream().filter(
                x -> x.getPermitIssuer().equals(code) || x.getPermitIssuedFor().equals(code))
                .map(x -> modelMapper.map(x, QuotaDto.class)).collect(Collectors.toList());

        dto.setQuotas(quotas);
        return dto;
    }

    @Transactional
    public void create(CreateAuthorityInput input, AuthorityConfig config) {
        log.info("Authority create command: {}", input);
        authorityRepository.findOneByCode(input.getCode()).ifPresent(s -> {
            throw new EpermitValidationException(ErrorCodes.AUTHORITY_ALREADY_EXISTS);
        });

        Authority authority = new Authority();
        authority.setPublicApiUri(input.getPublicApiUri());
        authority.setCode(input.getCode());
        authority.setName(input.getName());
        config.getKeys().forEach(k -> {
            AuthorityKey authorityKey = new AuthorityKey();
            authorityKey.setJwk(GsonUtil.getGson().toJson(k));
            authorityKey.setKeyId(k.getKid());
            authority.addKey(authorityKey);
        });
        log.info("Authority created: {}", authority);
        authorityRepository.save(authority);
    }

    @Transactional
    public void createQuota(String authorityCode, CreateQuotaInput input) {
        log.info("Quota create command: {}", input);
        authorityRepository.findOneByCode(authorityCode)
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.AUTHORITY_NOT_FOUND));
        String issuer = properties.getIssuerCode();
        String prevEventId = ledgerEventUtil.getPreviousEventId(authorityCode);
        QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent(issuer, authorityCode, prevEventId);
        event.setQuantity(input.getQuantity());
        event.setPermitType(input.getPermitType());
        event.setPermitYear(input.getPermitYear());
        event.setPermitIssuer(authorityCode);
        event.setPermitIssuedFor(issuer);
        ledgerEventUtil.persistAndPublishEvent(event);
        log.info("Quota created: {}", event);
    }
}
