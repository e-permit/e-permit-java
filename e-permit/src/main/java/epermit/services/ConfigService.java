package epermit.services;


import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.PublicJwk;
import epermit.models.dtos.TrustedAuthority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final AuthorityRepository authorityRepository;
    private final KeyRepository keyRepository;
    private final EPermitProperties properties;

    @SneakyThrows
    public AuthorityConfig getConfig() {
        AuthorityConfig dto = new AuthorityConfig();
        dto.setCode(properties.getIssuerCode());
        dto.setVerifyUri(properties.getIssuerVerifyUri());
        Gson gson = GsonUtil.getGson();
        List<PublicJwk> keyDtoList = new ArrayList<>();
        keyRepository.findAllByEnabledTrue().forEach(key -> {
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
}
