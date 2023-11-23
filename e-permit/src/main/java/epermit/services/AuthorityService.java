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
import epermit.entities.Authority;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityDto;
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

    public List<AuthorityDto> getAll() {
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        return all.stream().map(x -> modelMapper.map(x, AuthorityDto.class))
                .collect(Collectors.toList());
    }

    public AuthorityDto getByCode(String code) {
        Authority authority = authorityRepository.findOneByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        AuthorityDto dto = modelMapper.map(authority, AuthorityDto.class);
        List<epermit.entities.LedgerQuota> quotaEntities = ledgerQuotaRepository.findAll();

        List<QuotaDto> quotas = quotaEntities.stream().filter(
                x -> x.getPermitIssuer().equals(code) || x.getPermitIssuedFor().equals(code))
                .map(x -> modelMapper.map(x, QuotaDto.class)).collect(Collectors.toList());
        dto.setQuotas(quotas);
        return dto;
    }

    @Transactional
    public void create(CreateAuthorityInput input) {
        log.info("Authority create command: {}", input);
        authorityRepository.findOneByCode(input.getCode()).ifPresent(s -> {
            throw new EpermitValidationException(ErrorCodes.AUTHORITY_ALREADY_EXISTS);
        });

        Authority authority = new Authority();
        authority.setClientId(input.getClientId());
        authority.setCode(input.getCode());
        authority.setName(input.getName());

        log.info("Authority created: {}", authority);
        authorityRepository.save(authority);
    }

    @Transactional
    public void createQuota(CreateQuotaInput input) {
        log.info("Quota create command: {}", input);
        authorityRepository.findOneByCode(input.getAuthorityCode())
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.AUTHORITY_NOT_FOUND));
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
}
