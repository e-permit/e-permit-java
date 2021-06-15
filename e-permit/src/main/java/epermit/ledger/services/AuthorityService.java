package epermit.ledger.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.ledger.entities.Authority;
import epermit.ledger.entities.LedgerPublicKey;
import epermit.ledger.entities.LedgerQuota;
import epermit.ledger.models.dtos.AuthorityConfig;
import epermit.ledger.models.dtos.AuthorityDto;
import epermit.ledger.models.dtos.PublicJwk;
import epermit.ledger.models.inputs.CreateAuthorityInput;
import epermit.ledger.models.inputs.CreateQuotaInput;
import epermit.ledger.repositories.AuthorityRepository;
import epermit.ledger.repositories.LedgerPublicKeyRepository;
import epermit.ledger.repositories.LedgerQuotaRepository;
import epermit.ledger.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final LedgerQuotaRepository ledgerQuotaRepository;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void create(CreateAuthorityInput input, AuthorityConfig config) {
        log.info("Authority create command: {}", input);
        Authority authority = new Authority();
        authority.setApiUri(input.getApiUri());
        authority.setCode(config.getCode());
        authority.setName(config.getName());
        authority.setVerifyUri(config.getVerifyUri());
        config.getKeys().forEach(k -> {
            LedgerPublicKey publicKey = new LedgerPublicKey();
            publicKey.setJwk(GsonUtil.getGson().toJson(k));
            publicKey.setKeyId(k.getKid());
            ledgerPublicKeyRepository.save(publicKey);
        });
        log.info("Authority created: {}", authority);
        authorityRepository.save(authority);
    }

    @Transactional
    public void createQuota(CreateQuotaInput input) {
        log.info("Quota create command: {}", input);
        LedgerQuota quota = new LedgerQuota();
        quota.setEndNumber(input.getEndId());
        quota.setStartNumber(input.getStartId());
        quota.setPermitType(input.getPermitType());
        quota.setPermitYear(input.getPermitYear());
        quota.setActive(true);
        log.info("Quota created: {}", quota);
        ledgerQuotaRepository.save(quota);
    }
}

