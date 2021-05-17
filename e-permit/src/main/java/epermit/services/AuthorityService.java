package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.models.AuthorityConfig;
import epermit.models.AuthorityDto;
import epermit.models.CommandResult;
import epermit.models.CreateAuthorityInput;
import epermit.models.CreateQuotaInput;
import epermit.models.EPermitProperties;
import epermit.models.PublicJwk;
import epermit.models.PublicKey;
import epermit.models.TrustedAuthority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final KeyRepository keyRepository;
    private final ModelMapper modelMapper;
    private final EPermitProperties properties;

    public List<AuthorityDto> getAll() {
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        return all.stream().map(x -> modelMapper.map(x, AuthorityDto.class))
                .collect(Collectors.toList());
    }

    public AuthorityDto getByCode(String code) {
        Authority authority = authorityRepository.findOneByCode(code).get();
        return modelMapper.map(authority, AuthorityDto.class);
    }

    @SneakyThrows
    public AuthorityConfig getConfig() {
        AuthorityConfig dto = new AuthorityConfig();
        dto.setCode(properties.getIssuerCode());
        dto.setVerifyUri(properties.getIssuerVerifyUri());
        Gson gson = GsonUtil.getGson();
        List<PublicKey> keyDtoList = new ArrayList<>();
        keyRepository.findAll().forEach(key -> {
            PublicKey publicKey = new PublicKey();
            publicKey.setKeyId(key.getKeyId());
            publicKey.setValidFrom(key.getValidFrom());
            publicKey.setValidUntil(key.getValidUntil());
            publicKey.setJwk(gson.fromJson(key.getPublicJwk(), PublicJwk.class));
            keyDtoList.add(publicKey);
        });
        dto.setKeys(keyDtoList);
        List<TrustedAuthority> trustedAuthorities = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(authority -> {
            TrustedAuthority trustedAuthority = new TrustedAuthority();
            trustedAuthority.setCode(authority.getCode());
            List<PublicKey> publicKeys = new ArrayList<>();
            authority.getKeys().forEach(k -> {
                PublicKey publicKey = new PublicKey();
                PublicJwk publicJwk = gson.fromJson(k.getJwk(), PublicJwk.class);
                publicKey.setKeyId(k.getKeyId());
                publicKey.setValidFrom(k.getValidFrom());
                publicKey.setValidUntil(k.getValidUntil());
                publicKey.setJwk(publicJwk);
                publicKeys.add(publicKey);
            });
            trustedAuthority.setKeys(publicKeys);
            trustedAuthorities.add(trustedAuthority);
        });

        dto.setAuthorities(trustedAuthorities);
        return dto;
    }

    public CommandResult create(CreateAuthorityInput input) {
        log.info("Authority create command: " + input.getApiUri());
        return CommandResult.fail("fail");
    }

    public CommandResult createQuota(CreateQuotaInput input) {
        return CommandResult.fail("fail");
    }

    public CommandResult enableQuota(Long id) {
        return CommandResult.fail("fail");
    }
}
