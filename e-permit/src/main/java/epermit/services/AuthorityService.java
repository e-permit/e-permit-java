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
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.PublicJwk;
import epermit.models.dtos.TrustedAuthority;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.repositories.VerifierQuotaRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final VerifierQuotaRepository verifierQuotaRepository;
    private final QuotaCreatedEventFactory quotaCreatedEventFactory;
    private final KeyRepository keyRepository;
    private final ModelMapper modelMapper;
    private final EPermitProperties properties;

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
        Authority authority = authorityRepository.findOneByCode(code).get();
        return entityToDto(authority);
    }

    @SneakyThrows
    public AuthorityConfig getConfig() {
        AuthorityConfig dto = new AuthorityConfig();
        dto.setCode(properties.getIssuerCode());
        dto.setVerifyUri(properties.getIssuerVerifyUri());
        Gson gson = GsonUtil.getGson();
        List<PublicJwk> keyDtoList = new ArrayList<>();
        keyRepository.findAllByActiveTrue().forEach(key -> {
            keyDtoList.add(gson.fromJson(key.getPublicJwk(), PublicJwk.class));
        });
        dto.setKeys(keyDtoList);
        List<TrustedAuthority> trustedAuthorities = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(authority -> {
            TrustedAuthority trustedAuthority = new TrustedAuthority();
            trustedAuthority.setCode(authority.getCode());
            List<PublicJwk> publicKeys = new ArrayList<>();
            authority.getKeys().forEach(k -> {
                PublicJwk publicJwk = gson.fromJson(k.getJwk(), PublicJwk.class);
                publicKeys.add(publicJwk);
            });
            trustedAuthority.setKeys(publicKeys);
            trustedAuthorities.add(trustedAuthority);
        });

        dto.setTrustedAuthorities(trustedAuthorities);
        return dto;
    }

    @Transactional
    public void create(CreateAuthorityInput input, AuthorityConfig config) {
        log.info("Authority create command: " + input.getApiUri());
        Authority authority = new Authority();
        authority.setApiUri(input.getApiUri());
        authority.setCode(input.getCode());
        authority.setName(input.getName());
        authority.setVerifyUri(config.getVerifyUri());
        config.getKeys().forEach(k -> {
            AuthorityKey authorityKey = new AuthorityKey();
            authorityKey.setJwk(GsonUtil.getGson().toJson(k));
            authorityKey.setKeyId(k.getKid());
            authority.addKey(authorityKey);
        });
        authorityRepository.save(authority);
    }

    @Transactional
    public void createQuota(CreateQuotaInput input) {
        Optional<Authority> authorityR =
                authorityRepository.findOneByCode(input.getAuthorityCode());
        if (!authorityR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AUTHORITY_NOTFOUND");
        }
        Authority authority = authorityR.get();
        VerifierQuota quota = new VerifierQuota();
        quota.setEndNumber(input.getEndId());
        quota.setStartNumber(input.getStartId());
        quota.setPermitType(input.getPermitType());
        quota.setPermitYear(input.getPermitYear());
        quota.setAuthority(authority);
        authority.addVerifierQuota(quota);
        authorityRepository.save(authority);
    }

    @Transactional
    public void enableQuota(Integer id) {
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
