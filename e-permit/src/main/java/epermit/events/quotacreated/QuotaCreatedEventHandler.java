package epermit.events.quotacreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.entities.IssuerQuota;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service("QUOTA_CREATED")
@RequiredArgsConstructor
public class QuotaCreatedEventHandler implements EventHandler {
    private final AuthorityRepository authorityRepository;
    @SneakyThrows
    public void handle(Object e) {
        QuotaCreatedEvent event = (QuotaCreatedEvent)e;
        Authority authority = authorityRepository.findOneByCode(event.getIssuer()).get();
        IssuerQuota quota = new IssuerQuota();
        quota.setActive(true);
        quota.setAuthority(authority);
        quota.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        quota.setCurrentNumber(event.getStartNumber());
        quota.setEndNumber(event.getEndNumber());
        quota.setPermitType(event.getPermitType());
        quota.setStartNumber(event.getStartNumber());
        quota.setPermitYear(event.getPermitYear());
        authority.addIssuerQuota(quota);
        authorityRepository.save(authority);
    }
}