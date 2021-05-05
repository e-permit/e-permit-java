package epermit.events.quotacreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.entities.Authority;
import epermit.entities.IssuerQuota;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

@Service("QUOTA_CREATED")
public class QuotaCreatedEventHandler implements EventHandler {
    private final QuotaCreatedEventValidator validator;
    private final AuthorityRepository repository;

    public QuotaCreatedEventHandler(AuthorityRepository repository,
            QuotaCreatedEventValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @SneakyThrows
    public EventHandleResult handle(String payload) {
        QuotaCreatedEvent e = JsonUtil.getGson().fromJson(payload, QuotaCreatedEvent.class);
        Boolean valid = validator.validate(e);
        if (!valid) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
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
        return EventHandleResult.success();
    }
}
