package epermit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final IssuedPermitRepository issuedPermitRepository;
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;

    public String getPermitId(String iss, String aud, PermitType pt, Integer py, int serialNumber) {
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
        Optional<IssuedPermit> revokedPermitR =
                issuedPermitRepository.findFirstByIssuedForAndRevokedTrue(issuedFor);
        if (revokedPermitR.isPresent()) {
            IssuedPermit revokedPermit = revokedPermitR.get();
            log.info("Revoked permit found. The permit id is {}", revokedPermit.getPermitId());
            int nextSerialNumber = revokedPermit.getSerialNumber();
            issuedPermitRepository.delete(revokedPermit);
            return Optional.of(nextSerialNumber);
        }
        Authority authority = authorityRepository.findOneByCode(issuedFor);
        Optional<IssuerQuota> quotaResult = authority.getIssuerQuotas().stream()
                .filter(x -> x.getPermitYear() == py && x.isActive() && x.getPermitType() == pt)
                .findFirst();
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

    public String generateQrCode(IssuedPermit permit) {
        log.info("generateQrCode started with {}", permit);
        String verifyUri = authorityRepository.findOneByCode(permit.getIssuedFor()).getVerifyUri();
        Map<String, String> claims = new HashMap<>();
        claims.put("id", permit.getPermitId());
        claims.put("iat", permit.getIssuedAt());
        claims.put("exp", permit.getExpireAt());
        claims.put("pn", permit.getPlateNumber());
        claims.put("cn", permit.getCompanyName());
        String jws = jwsUtil.createJws(claims);
        String qrCode = verifyUri + "#" + properties.getQrcodeVersion() + "." + jws;
        log.info("generateQrCode ended jws is {}", jws);
        return qrCode;
    }

}
