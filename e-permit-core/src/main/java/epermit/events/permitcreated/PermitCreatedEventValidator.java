package epermit.events.permitcreated;

import java.util.Optional;
import epermit.common.PermitUtil;
import epermit.common.PermitProperties;
import epermit.entities.Authority;
import epermit.entities.VerifierQuota;
import epermit.repositories.AuthorityRepository;

public class PermitCreatedEventValidator {
    private final AuthorityRepository authorityRepository;
    private final PermitProperties props;

    public PermitCreatedEventValidator(AuthorityRepository authorityRepository, PermitProperties props) {
        this.authorityRepository = authorityRepository;
        this.props = props;
    }

    public Boolean validate(PermitCreatedEvent event) {
        if (!props.getIssuerCode().equals(event.getIssuedFor())) {
            return false;
            //return EventHandleResult.fail("INVALID_ISSUER");
        }
        String expectedSerialNumber = PermitUtil.getSerialNumber(event.getIssuedFor(), event.getIssuer(),
        event.getPermitType(), event.getPermitYear(), event.getPermitId());
        if (!expectedSerialNumber.equals(event.getSerialNumber())) {
            //return EventHandleResult.fail("INVALID_SERIALNUMBER");
            return false;
        }
        Authority authority = authorityRepository.findByCode(event.getIssuer()).get();
        Optional<VerifierQuota> quotaResult = authority.getVerifierQuotas().stream()
                .filter(x -> x.getPermitYear() == event.getPermitYear()
                        && event.getPermitId() < x.getEndNumber()
                        && event.getPermitId() > x.getStartNumber()
                        && x.getPermitType() == event.getPermitType())
                .findAny();
        if (!quotaResult.isPresent()) {
            //return EventHandleResult.fail("QUOTA_DOESNT_MATCH");
            return false;
        }
        return true;
    }
}