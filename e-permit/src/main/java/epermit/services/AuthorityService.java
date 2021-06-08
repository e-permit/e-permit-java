package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.VerifierQuota;
import epermit.events.quotacreated.QuotaCreatedEventFactory;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.PublicJwk;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.VerifierQuotaRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final VerifierQuotaRepository verifierQuotaRepository;
    private final QuotaCreatedEventFactory quotaCreatedEventFactory;
    private final ModelMapper modelMapper;

    private AuthorityDto entityToDto(Authority authority) {
        AuthorityDto dto = modelMapper.map(authority, AuthorityDto.class);
        for (int i = 0; i < dto.getKeys().size(); i++) {
            PublicJwk jwk = GsonUtil.getGson().fromJson(authority.getKeys().get(i).getJwk(),
                    PublicJwk.class);
            dto.getKeys().set(i, jwk);
        }
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
        authority.setVerifyUri(config.getVerifyUri());
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
    public void createQuota(CreateQuotaInput input) {
        log.info("Quota create command: {}", input);
        Authority authority =
                authorityRepository.findOneByCode(input.getAuthorityCode());
        VerifierQuota quota = new VerifierQuota();
        quota.setEndNumber(input.getEndId());
        quota.setStartNumber(input.getStartId());
        quota.setPermitType(input.getPermitType());
        quota.setPermitYear(input.getPermitYear());
        quota.setAuthority(authority);
        authority.addVerifierQuota(quota);
        log.info("Quota created: {}", quota);
        authorityRepository.save(authority);
    }

    @Transactional
    public void enableQuota(Integer id) {
        log.info("Enable Quota command: {}", id);
        Optional<VerifierQuota> quotaR = verifierQuotaRepository.findById(id);
        if (!quotaR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "VERIFIER_QUOTA_NOTFOUND");
        }
        VerifierQuota quota = quotaR.get();
        quota.setActive(true);
        verifierQuotaRepository.save(quota);
        quotaCreatedEventFactory.create(quota);
    }
}
