package epermit.services;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.PublicJwk;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final EPermitProperties properties;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @SneakyThrows
    public AuthorityConfig getConfig() {
        AuthorityConfig config = new AuthorityConfig();
        config.setCode(properties.getIssuerCode());
        config.setName(properties.getIssuerName());
        Gson gson = GsonUtil.getGson();
        List<PublicJwk> keyDtoList = new ArrayList<>();
        List<LedgerPublicKey> keys = ledgerPublicKeyRepository
                .findAllByPartnerAndRevokedFalse(properties.getIssuerCode());
        keys.forEach(key -> {
            keyDtoList.add(gson.fromJson(key.getJwk(), PublicJwk.class));
        });
        config.setKeys(keyDtoList);
        return config;
    }
}
