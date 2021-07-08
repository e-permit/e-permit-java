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
import epermit.entities.LedgerQuota;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.models.inputs.CreateQrCodeInput;
import epermit.models.inputs.QuotaSufficientInput;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final LedgerQuotaRepository quotaRepository;
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

        String verifyUri = "https://e-permit.github.io/verify";
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

    public LedgerQuota getLedgerQuota(String issuedFor,
            PermitType pt, int py, List<Integer> usedIds) {
        Optional<LedgerQuota> ledgerQuota = quotaRepository.findAll().stream()
                .filter(x -> x.getIssuer().equals(properties.getIssuerCode())
                        && x.getIssuedFor().equals(issuedFor) && x.getPermitType() == pt
                        && x.getPermitYear() == py && !usedIds.contains(x.getId()))
                .findFirst();
        if(ledgerQuota.isPresent()){
            return ledgerQuota.get();
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quota is not available");
        }
        /*if (issuerQuota.getActiveLedgerQuotaId() == null) {
            Optional<LedgerQuota> ledgerQuotaResult = ledgerQuotas.stream()
                    .filter(x -> issuerQuota.getUsedLedgerQuotaIds().contains(x.getId()))
                    .findFirst();
            if (!ledgerQuotaResult.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quota is not available");
            }
            ledgerQuota = ledgerQuotaResult.get();
            issuerQuota.setActiveLedgerQuotaId(ledgerQuota.getId());
            issuerQuota.setNextNumber(ledgerQuota.getStartNumber());
            issuerQuota.getUsedLedgerQuotaIds().add(ledgerQuota.getId());
        } else {
            ledgerQuota = quotaRepository.findById(issuerQuota.getActiveLedgerQuotaId()).get();
        }*/
    }

    
}
