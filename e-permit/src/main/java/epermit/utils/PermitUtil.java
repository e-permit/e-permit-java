package epermit.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.entities.VerifierQuota;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final IssuedPermitRepository issuedPermitRepository;
    private final AuthorityRepository authorityRepository;

    public String getPermitId(String iss, String aud, PermitType pt, Integer py, int serialNumber) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(iss).add(aud).add(Integer.toString(py))
                .add(Integer.toString(pt.getCode())).add(Long.toString(serialNumber)).toString();
        return permitId;
    }

    public boolean isQuotaSufficient(String issuer, int permitYear, int serialNumber,
            PermitType permitType) {
        Authority authority = authorityRepository.findOneByCode(issuer).get();
        return authority.getVerifierQuotas().stream().anyMatch(x -> x.getPermitType() == permitType
                && serialNumber >= x.getStartNumber() && serialNumber <= x.getEndNumber());
    }

    public Integer generateSerialNumber(String issuedFor, int py, PermitType pt) {
        Optional<IssuedPermit> revokedCred =
                issuedPermitRepository.findFirstByIssuedForAndRevokedTrue(issuedFor);
        if (revokedCred.isPresent()) {
            int nextPid = revokedCred.get().getSerialNumber();
            issuedPermitRepository.delete(revokedCred.get());
            return nextPid;
        }
        Optional<Authority> authority = authorityRepository.findOneByCode(issuedFor);
        Optional<IssuerQuota> quotaResult = authority.get().getIssuerQuotas().stream()
                .filter(x -> x.getPermitYear() == py && x.isActive() && x.getPermitType() == pt)
                .findFirst();
        if (quotaResult.isPresent()) {
            IssuerQuota quota = quotaResult.get();
            int nextPid = quota.getCurrentNumber() + 1;
            quota.setCurrentNumber(nextPid);
            if (quota.getCurrentNumber() == quota.getEndNumber()) {
                quota.setActive(false);
            }
            authorityRepository.save(authority.get());
            return nextPid;
        }
        return null;
    }

    public String generateQrCode(IssuedPermit permit) {
        Map<String, String> claims = new HashMap<>();
        claims.put("id", permit.getPermitId());
        claims.put("iat", permit.getIssuedAt());
        claims.put("exp", permit.getExpireAt());
        claims.put("pn", permit.getPlateNumber());
        claims.put("cn", permit.getCompanyName());
        return jwsUtil.createJws(claims);
    }

}
