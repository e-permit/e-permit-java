package epermit.events.permitcreated;

import java.util.Optional;

import org.springframework.stereotype.Component;

import epermit.common.PermitUtil;
import epermit.common.PermitProperties;
import epermit.entities.Authority;
import epermit.entities.VerifierQuota;
import epermit.repositories.AuthorityRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PermitCreatedEventValidator {
    private final AuthorityRepository authorityRepository;

    public PermitCreatedEventValidator(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public Boolean validate(PermitCreatedEvent event) {
        String expectedPermitId = PermitUtil.getPermitId(event.getIssuedFor(), event.getIssuer(), event.getPermitType(),
                event.getPermitYear(), event.getSerialNumber());
        if (!expectedPermitId.equals(event.getPermitId())) {
            log.info("INVALID_PERMITID");
            return false;
        }
        Authority authority = authorityRepository.findByCode(event.getIssuer()).get();
        Optional<VerifierQuota> quotaResult = authority.getVerifierQuotas().stream()
                .filter(x -> x.getPermitYear() == event.getPermitYear() && event.getSerialNumber() < x.getEndNumber()
                        && event.getSerialNumber() > x.getStartNumber() && x.getPermitType() == event.getPermitType())
                .findAny();
        if (!quotaResult.isPresent()) {
            log.info("QUOTA_DOESNT_MATCH");
            return false;
        }
        return true;
    }
}