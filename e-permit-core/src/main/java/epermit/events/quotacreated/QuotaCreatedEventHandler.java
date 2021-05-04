package epermit.events.quotacreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.entities.IssuerQuota;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

@Service("QUOTA_CREATED")
public class QuotaCreatedEventHandler {

    private final AuthorityRepository repository;

    public QuotaCreatedEventHandler(AuthorityRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    public void handle(QuotaCreatedEvent e) {
        Authority authority = repository.findByCode(e.getIssuer()).get();
        IssuerQuota quota = new IssuerQuota();
        quota.setActive(true);
        quota.setAuthority(authority);
        quota.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        quota.setCurrentNumber(e.getStartId());
        quota.setEndNumber(e.getEndId());
        quota.setPermitType(e.getPermitType());
        quota.setStartNumber(e.getStartId());
        quota.setPermitYear(e.getPermitYear());
        authority.addIssuerQuota(quota);
        repository.save(authority);
    }
}
