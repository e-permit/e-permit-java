package epermit.services;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.PublicJwk;
import epermit.models.dtos.TrustedAuthority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @SneakyThrows
    public AuthorityConfig getConfig() {
        AuthorityConfig dto = new AuthorityConfig();
        dto.setCode(properties.getIssuerCode());
        dto.setName(properties.getIssuerName());
        Gson gson = GsonUtil.getGson();
        List<PublicJwk> keyDtoList = new ArrayList<>();
        // Get all revoked keys and set revoked list (id-validfrom-validto)
        List<LedgerPublicKey> keys = ledgerPublicKeyRepository
                .findAllByAuthorityCodeAndRevokedFalse(properties.getIssuerCode());
        keys.forEach(key -> {
            keyDtoList.add(gson.fromJson(key.getJwk(), PublicJwk.class));
        });
        dto.setKeys(keyDtoList);
        return dto;
    }

    public List<TrustedAuthority> getTrustedAuthorities() {
        Gson gson = GsonUtil.getGson();
        List<TrustedAuthority> trustedAuthorities = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(authority -> {
            TrustedAuthority trustedAuthority = new TrustedAuthority();
            trustedAuthority.setCode(authority.getCode());
            List<PublicJwk> publicKeys = new ArrayList<>();
            ledgerPublicKeyRepository.findAllByAuthorityCodeAndRevokedFalse(authority.getCode())
                    .forEach(k -> {
                        PublicJwk publicJwk = gson.fromJson(k.getJwk(), PublicJwk.class);
                        publicKeys.add(publicJwk);
                    });
            trustedAuthority.setKeys(publicKeys);
            trustedAuthorities.add(trustedAuthority);
        });
        return trustedAuthorities;
    }
}
