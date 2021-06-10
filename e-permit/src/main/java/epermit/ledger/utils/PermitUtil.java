package epermit.ledger.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import epermit.ledger.entities.Permit;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.models.enums.PermitType;
import epermit.ledger.models.inputs.CreatePermitIdInput;
import epermit.ledger.models.inputs.CreateQrCodeInput;
import epermit.ledger.repositories.PermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final PermitRepository permitRepository;
    private final EPermitProperties properties;

    public String getPermitId(CreatePermitIdInput input) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(iss).add(aud).add(Integer.toString(py)).add(pt.getCode())
                .add(Long.toString(serialNumber)).toString();
        return permitId;
    }

    public boolean isQuotaSufficient(String issuer, int permitYear, int serialNumber,
            PermitType permitType) {
        Authority authority = authorityRepository.findOneByCode(issuer);
        Boolean r = authority.getVerifierQuotas().stream()
                .anyMatch(x -> x.isActive() && x.getPermitType() == permitType
                        && serialNumber >= x.getStartNumber() && serialNumber <= x.getEndNumber());
        log.info("isQuotaSufficient ruslt is {}", r);
        return r;
    }

    public Optional<Integer> generateSerialNumber(String issuedFor, int py, PermitType pt) {
        Optional<Permit> revokedPermitR =
                permitRepository.findFirstByIssuedForAndRevokedTrue(issuedFor);
        if (revokedPermitR.isPresent()) {
            Permit revokedPermit = revokedPermitR.get();
            log.info("Revoked permit found. The permit id is {}", revokedPermit.getPermitId());
            int nextSerialNumber = revokedPermit.getSerialNumber();
            revokedPermit.setDeleted(true);
            permitRepository.save(revokedPermit);
            return Optional.of(nextSerialNumber);
        }
        Optional<Quota> quotaResult = q
        if (quotaResult.isPresent()) {
            IssuerQuota quota = quotaResult.get();
            log.info("Quota found. The quota id is {}", quota.getId());
            int nextSerialNumber = quota.getNextNumber();
            if (nextSerialNumber + 1 > quota.getEndNumber()) {
                log.info("Quota ended");
                quota.setActive(false);
            } else {
                log.info("Next serial number is valid", nextSerialNumber);
                quota.setNextNumber(nextSerialNumber + 1);
            }
            authorityRepository.save(authority);
            return Optional.of(nextSerialNumber);
        }
        log.info(
                "There is no sufficient quota for the authority {}, permit year {} and permit type {}",
                issuedFor, py, pt);
        return Optional.empty();
    }

    public String generateQrCode(CreateQrCodeInput input) {
        log.info("generateQrCode started with {}");
        String verifyUri = authorityRepository.findOneByCode(input.getIssuedFor()).getVerifyUri();
        Map<String, String> claims = new HashMap<>();
        claims.put("id", permitId);
        claims.put("iat", issuedAt);
        claims.put("exp", expireAt);
        claims.put("pn", plateNumber);
        claims.put("cn", companyName);
        String jws = jwsUtil.createJws(claims);
        String qrCode = verifyUri + "#" + properties.getQrcodeVersion() + "." + jws;
        log.info("generateQrCode ended jws is {}", jws);
        return qrCode;
    }

}
