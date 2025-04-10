package epermit.services;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.AuthorityListItem;
import epermit.models.dtos.HealthCheckResultItem;
import epermit.models.dtos.HealthCheckRemoteResult;
import epermit.models.dtos.HealthCheckResult;
import epermit.models.dtos.QuotaDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerEventRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.utils.JwsUtil;
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
    private final LedgerEventRepository ledgerEventRepository;
    private final ModelMapper modelMapper;
    private final JwsUtil jwsUtil;
    public final ObjectMapper objectMapper;

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

    public HealthCheckResult healthcheck() {
        HealthCheckResult result = new HealthCheckResult();
        result.setOk(true);
        result.setAuthorities(new ArrayList<>());
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(3))
                .messageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        all.forEach(authority -> {
            HealthCheckResultItem resultItem = new HealthCheckResultItem();
            resultItem.setAuthority(authority.getCode());
            resultItem.setOk(true);

            try {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String jwt = jwsUtil.createJwt(authority.getCode());
                headers.add("Authorization", "Bearer " + jwt);
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                LedgerEvent to = ledgerEventRepository
                        .findTopByProducerAndConsumerOrderByCreatedAtDesc(authority.getCode(),
                                properties.getIssuerCode())
                        .orElse(new LedgerEvent());
                LedgerEvent from = ledgerEventRepository
                        .findTopByProducerAndConsumerOrderByCreatedAtDesc(properties.getIssuerCode(),
                                authority.getCode())
                        .orElse(new LedgerEvent());

                ResponseEntity<HealthCheckRemoteResult> resp = restTemplate
                        .exchange(authority.getPublicApiUri() + "/healthcheck", HttpMethod.GET, entity,
                                HealthCheckRemoteResult.class);
                HealthCheckRemoteResult r = resp.getBody();
                if (!to.getEventId().equals(r.getFromLastEventId())) {
                    log.info("Event sync problem from {} with {} to {} with {}",
                            authority.getCode(), r.getFromLastEventId(), properties.getIssuerCode(), to.getEventId());
                    resultItem.setOk(false);
                    resultItem.setProblem(
                            "Event sync problem from " + authority.getCode() + " to " + properties.getIssuerCode());
                }
                if (!from.getEventId().equals(r.getToLastEventId())) {
                    log.info("Event sync problem from {} with {} to {} with {}",
                         properties.getIssuerCode(), from.getEventId(), authority.getCode(), r.getToLastEventId());
                    resultItem.setOk(false);
                    resultItem.setProblem(
                            "Event sync problem from " + properties.getIssuerCode() + " to " + authority.getCode());
                }

            } catch (Exception e) {
                resultItem.setOk(false);
                resultItem.setProblem(e.getMessage());
            }
            if (!resultItem.isOk()) {
                result.setOk(false);
            }
            result.getAuthorities().add(resultItem);
        });
        return result;
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
