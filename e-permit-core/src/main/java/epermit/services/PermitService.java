package epermit.services;

import java.util.Optional;

import org.springframework.stereotype.Component;

import epermit.common.PermitProperties;
import epermit.common.PermitType;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;

@Component
public class PermitService {

    private final AuthorityRepository authorityRepository;
    private final IssuedPermitRepository issuedCredentialRepository;
    private final PermitProperties props;
    private final KeyService keyService;

    public PermitService(AuthorityRepository authorityRepository, IssuedPermitRepository issuedCredentialRepository,
            PermitProperties props, KeyService keyService) {
        this.authorityRepository = authorityRepository;
        this.issuedCredentialRepository = issuedCredentialRepository;
        this.keyService = keyService;
        this.props = props;
    }

    public Integer generateSerialNumber(String issuedFor, int py, PermitType pt) {
        Optional<IssuedPermit> revokedCred = issuedCredentialRepository.findFirstByRevokedTrue();
        if (revokedCred.isPresent()) {
            int nextPid = revokedCred.get().getSerialNumber();
            issuedCredentialRepository.delete(revokedCred.get());
            return nextPid;
        }
        Optional<Authority> authority = authorityRepository.findByCode(issuedFor);
        Optional<IssuerQuota> quotaResult = authority.get().getIssuerQuotas().stream()
                .filter(x -> x.getPermitYear() == py && x.isActive() && x.getPermitType() == pt).findFirst();
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
        String iss = props.getIssuerCode();
        String aud = permit.getIssuedFor();
        String year = Integer.toString(permit.getPermitYear());
        String pt = Integer.toString(permit.getPermitType().getCode());
        String pid = Integer.toString(permit.getSerialNumber());
        String iat = permit.getIssuedAt();
        String exp = permit.getExpireAt();
        String sub = permit.getPlateNumber();
        String cn = permit.getCompanyName();
        String payload = String.join("#", iss, aud, year, pt, pid, iat, exp, sub, cn);
        return keyService.createJws(payload);
    }
}
