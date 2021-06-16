package epermit.ledger.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.ledger.entities.Authority;
import epermit.ledger.entities.LedgerQuota;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.models.enums.PermitType;
import epermit.ledger.models.inputs.CreatePermitIdInput;
import epermit.ledger.models.inputs.CreateQrCodeInput;
import epermit.ledger.models.inputs.QuotaSufficientInput;
import epermit.ledger.models.valueobjects.AuthorityQuota;
import epermit.ledger.repositories.AuthorityRepository;
import epermit.ledger.repositories.LedgerPermitRepository;
import epermit.ledger.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final LedgerQuotaRepository quotaRepository;
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;

    public String getPermitId(CreatePermitIdInput input) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId =
                joiner.add(input.getIssuer()).add(input.getIssuedFor()).add(input.getPermitYear())
                        .add(input.getPermitType()).add(input.getSerialNumber()).toString();
        return permitId;
    }

    public boolean isQuotaSufficient(QuotaSufficientInput input) {
        Boolean r = quotaRepository.findAll().stream()
                .anyMatch(x -> x.getIssuer().equals(input.getIssuer())
                        && x.getIssuedFor().equals(input.getIssuedFor()) && x.isActive()
                        && x.getPermitType() == input.getPermitType()
                        && input.getSerialNumber() >= x.getStartNumber()
                        && input.getSerialNumber() <= x.getEndNumber());
        log.info("isQuotaSufficient rusult is {}", r);
        return r;
    }

    public Optional<Integer> generateSerialNumber(String issuedFor, int py, PermitType pt) {
        Authority authority = authorityRepository.findOneByCode(issuedFor);
        Optional<AuthorityQuota> quotaR = authority.getQuotas().stream()
                .filter(x -> x.getPermitType() == pt && x.getPermitYear() == py).findFirst();
        AuthorityQuota quota;
        if (!quotaR.isPresent()) {
            quota = new AuthorityQuota();
            quota.setPermitType(pt);
            quota.setPermitYear(py);
            authority.getQuotas().add(quota);
        } else {
            quota = quotaR.get();
        }
        if (!quota.getSerialNumbers().isEmpty()) {
            Integer n = quota.getSerialNumbers().get(0);
            quota.getSerialNumbers().remove(0);
            authorityRepository.save(authority);
            return Optional.of(n);
        }
        List<LedgerQuota> quotas = getLedgerQuotas(issuedFor, pt, py);
        LedgerQuota ledgerQuota;
        if (quota.getActiveQuotaId() == null) {
            Optional<LedgerQuota> ledgerQuotaResult = quotas.stream()
                    .filter(x -> quota.getUsedQuotaIds().contains(x.getId())).findFirst();
            if (!ledgerQuotaResult.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
            }
            ledgerQuota = ledgerQuotaResult.get();
            quota.setActiveQuotaId(ledgerQuota.getId());
            quota.setNextNumber(ledgerQuota.getStartNumber());
            quota.getUsedQuotaIds().add(ledgerQuota.getId());
        } else {
            ledgerQuota = quotaRepository.findById(quota.getActiveQuotaId()).get();
        }
        int nextSerialNumber = quota.getNextNumber();
        if (nextSerialNumber == ledgerQuota.getEndNumber()) {
            log.info("Quota ended");
            // quota.setActive(false);
        } else {
            log.info("Next serial number is valid", nextSerialNumber);
            quota.setNextNumber(nextSerialNumber + 1);
        }
        return Optional.empty();
    }

    private List<LedgerQuota> getLedgerQuotas(String issuedFor, PermitType pt, int py) {
        List<LedgerQuota> ledgerQuotas = quotaRepository.findAll().stream()
                .filter(x -> x.getIssuer().equals(properties.getIssuerCode())
                        && x.getIssuedFor().equals(issuedFor) && x.getPermitType() == pt
                        && x.getPermitYear() == py)
                .collect(Collectors.toList());
        return ledgerQuotas;
    }

    public String generateQrCode(CreateQrCodeInput input) {
        log.info("generateQrCode started with {}", input);

        String verifyUri = authorityRepository.findOneByCode(input.getIssuedFor()).getVerifyUri();
        Map<String, String> claims = new HashMap<>();
        claims.put("id", input.getId());
        claims.put("iat", input.getIssuedAt());
        claims.put("exp", input.getExpireAt());
        claims.put("pn", input.getPlateNumber());
        claims.put("cn", input.getCompanyName());
        String jws = jwsUtil.createJws(claims);
        String qrCode = verifyUri + "#" + properties.getQrcodeVersion() + "." + jws;
        log.info("generateQrCode ended jws is {}", jws);

        return qrCode;
    }

}
