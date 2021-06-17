package epermit.services;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import com.google.gson.Gson;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.entities.Authority;
import epermit.entities.PrivateKey;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.PublicJwk;
import epermit.models.dtos.TrustedAuthority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.GsonUtil;
import epermit.utils.PrivateKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {
    private final AuthorityRepository authorityRepository;
    private final PrivateKeyRepository keyRepository;
    private final EPermitProperties properties;
    private final PrivateKeyUtil keyUtil;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Transactional
    @SneakyThrows
    public void seed() {
        Long keyCount = keyRepository.count();
        if (keyCount == 0) {
            PrivateKey key;
            String privateKey = properties.getIssuerPrivateKey();
            if (privateKey != null) {
                String jwkStr = new String(Base64.getUrlDecoder().decode(privateKey));
                log.info("Private key exist");
                key = keyUtil.create(ECKey.parse(jwkStr));
            } else {
                key = keyUtil.create("1");
            }
            key.setEnabled(true);
            keyRepository.save(key);
        }
    }

    @SneakyThrows
    public AuthorityConfig getConfig() {
        AuthorityConfig dto = new AuthorityConfig();
        dto.setCode(properties.getIssuerCode());
        dto.setVerifyUri(properties.getIssuerVerifyUri());
        Gson gson = GsonUtil.getGson();
        List<PublicJwk> keyDtoList = new ArrayList<>();
        ledgerPublicKeyRepository.findAllByAuthorityCodeAndRevokedFalse(properties.getIssuerCode())
                .forEach(key -> {
                    keyDtoList.add(gson.fromJson(key.getJwk(), PublicJwk.class));
                });
        dto.setKeys(keyDtoList);
        List<TrustedAuthority> trustedAuthorities = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(authority -> {
            TrustedAuthority trustedAuthority = new TrustedAuthority();
            trustedAuthority.setCode(authority.getCode());
            List<PublicJwk> publicKeys = new ArrayList<>();
            ledgerPublicKeyRepository.findAllByAuthorityCodeAndRevokedFalse(authority.getCode()).forEach(k -> {
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
