package epermit.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.AuthorityIssuerQuota;
import epermit.entities.LedgerQuota;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.models.inputs.CreateQrCodeInput;
import epermit.models.inputs.QuotaSufficientInput;
import epermit.models.valueobjects.AuthorityIssuerQuotaPayload;
import epermit.repositories.AuthorityIssuerQuotaRepository;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final LedgerQuotaRepository quotaRepository;
    private final AuthorityIssuerQuotaRepository authorityIssuerQuotaRepository;
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;

    public String getPermitId(CreatePermitIdInput input) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(input.getIssuer()).add(input.getIssuedFor())
                .add(Integer.toString(input.getPermitYear())).add(input.getPermitType().getCode())
                .add(Integer.toString(input.getSerialNumber())).toString();
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

    public LedgerQuota getActiveLedgerQuota(AuthorityIssuerQuotaPayload issuerQuotaPayload,
            String issuedFor, PermitType pt, int py) {
        List<LedgerQuota> ledgerQuotas = quotaRepository.findAll().stream()
                .filter(x -> x.getIssuer().equals(properties.getIssuerCode())
                        && x.getIssuedFor().equals(issuedFor) && x.getPermitType() == pt
                        && x.getPermitYear() == py)
                .collect(Collectors.toList());
        LedgerQuota ledgerQuota;
        if (issuerQuotaPayload.getActiveQuotaId() == null) {
            Optional<LedgerQuota> ledgerQuotaResult = ledgerQuotas.stream()
                    .filter(x -> issuerQuotaPayload.getUsedQuotaIds().contains(x.getId()))
                    .findFirst();
            if (!ledgerQuotaResult.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quota is not availabe");
            }
            ledgerQuota = ledgerQuotaResult.get();
            issuerQuotaPayload.setActiveQuotaId(ledgerQuota.getId());
            issuerQuotaPayload.setNextNumber(ledgerQuota.getStartNumber());
            issuerQuotaPayload.getUsedQuotaIds().add(ledgerQuota.getId());
        } else {
            ledgerQuota = quotaRepository.findById(issuerQuotaPayload.getActiveQuotaId()).get();
        }
        return ledgerQuota;
    }

    public AuthorityIssuerQuota getIssuerQuota(String aud, PermitType pt, int py) {
        List<AuthorityIssuerQuota> issuerQuotas = authorityIssuerQuotaRepository
                .findAll().stream().filter(x -> x.getAuthority().getCode().equals(aud)
                        && x.getPermitType() == pt && x.getPermitYear() == py)
                .collect(Collectors.toList());
        AuthorityIssuerQuota quota;
        if (issuerQuotas.isEmpty()) {
            quota = new AuthorityIssuerQuota();
            quota.setPermitType(pt);
            quota.setPermitYear(py);
            quota.setPayload(new AuthorityIssuerQuotaPayload());

        } else {
            quota = issuerQuotas.get(0);
        }
        return quota;
    }
}
