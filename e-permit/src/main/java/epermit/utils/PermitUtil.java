package epermit.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.models.CreatePermitInput;
import epermit.models.EPermitProperties;
import epermit.models.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final EPermitProperties properties;
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
        return false;
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
        String iss = properties.getIssuerCode();
        String aud = permit.getIssuedFor();
        String year = Integer.toString(permit.getPermitYear());
        String pt = Integer.toString(permit.getPermitType().getCode());
        String pid = Integer.toString(permit.getSerialNumber());
        String iat = permit.getIssuedAt();
        String exp = permit.getExpireAt();
        String sub = permit.getPlateNumber();
        String cn = permit.getCompanyName();
        String payload = String.join("#", iss, aud, year, pt, pid, iat, exp, sub, cn);
        return jwsUtil.createJws(payload);
    }

}
