package epermit.services;

import java.util.Optional;

import org.springframework.stereotype.Component;

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

    public PermitService(AuthorityRepository authorityRepository,
            IssuedPermitRepository issuedCredentialRepository) {
        this.authorityRepository = authorityRepository;
        this.issuedCredentialRepository = issuedCredentialRepository;
    }
    
    public  Integer generatePermitId(String issuedFor, int py, PermitType pt) {
        Optional<IssuedPermit> revokedCred = issuedCredentialRepository.findFirstByRevokedTrue();
        if (revokedCred.isPresent()) {
            int nextPid = revokedCred.get().getPermitId();
            issuedCredentialRepository.delete(revokedCred.get());
            return nextPid;
        }
        Optional<Authority> authority = authorityRepository.findByCode(issuedFor);
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

    public String generateQrCode(IssuedPermit permit){
        return "";
    }
}
