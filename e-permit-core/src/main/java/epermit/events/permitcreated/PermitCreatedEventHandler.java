package epermit.events.permitcreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.common.PermitUtil;
import epermit.entities.Authority;
import epermit.entities.Permit;
import epermit.entities.VerifierQuota;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PermitRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED")
public class PermitCreatedEventHandler implements EventHandler {

    private final PermitRepository repository;
    private final AuthorityRepository authorityRepository;

    public PermitCreatedEventHandler(PermitRepository repository,
            AuthorityRepository authorityRepository) {
        this.repository = repository;
        this.authorityRepository = authorityRepository;
    }

    @SneakyThrows
    public EventHandleResult handle(String payload) {
        PermitCreatedEvent event = JsonUtil.getGson().fromJson(payload, PermitCreatedEvent.class);
        EventHandleResult validation = validate(event);
        if (!validation.isSucceed()) {
            return validation;
        }
        Gson gson = JsonUtil.getGson();
        Permit permit = new Permit();
        if (event.getClaims() != null && !event.getClaims().isEmpty()) {
            permit.setClaims(gson.toJson(event.getClaims()));
        }
        permit.setCompanyName(event.getCompanyName());
        permit.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        permit.setExpireAt(event.getExpireAt());
        permit.setIssuedAt(event.getIssuedAt());
        permit.setIssuer(event.getIssuer());
        permit.setPermitId(event.getPermitId());
        permit.setPermitType(event.getPermitType());
        permit.setPermitYear(event.getPermitYear());
        permit.setPlateNumber(event.getPlateNumber());
        permit.setSerialNumber(event.getSerialNumber());
        repository.save(permit);
        return EventHandleResult.success();
    }

    private EventHandleResult validate(PermitCreatedEvent event) {
        String expectedPermitId = PermitUtil.getPermitId(event.getIssuer(), event.getIssuedFor(),
                event.getPermitType(), event.getPermitYear(), event.getSerialNumber());
        if (!expectedPermitId.equals(event.getPermitId())) {
            log.info("INVALID_PERMITID");
            return EventHandleResult.fail("INVALID_PERMITID");
        }
        Optional<Permit> exist = repository.findOneByPermitId(event.getPermitId());
        if (exist.isPresent()) {
            log.info("PERMIT_EXIST");
            return EventHandleResult.fail("PERMIT_EXIST");
        }
        Authority authority = authorityRepository.findOneByCode(event.getIssuer()).get();
        Optional<VerifierQuota> quotaResult = authority.getVerifierQuotas().stream()
                .filter(x -> x.getPermitYear() == event.getPermitYear()
                        && event.getSerialNumber() <= x.getEndNumber()
                        && event.getSerialNumber() >= x.getStartNumber()
                        && x.getPermitType() == event.getPermitType())
                .findAny();
        if (!quotaResult.isPresent()) {
            log.info("QUOTA_DOESNT_MATCH");
            return EventHandleResult.fail("QUOTA_DOESNT_MATCH");
        }
        return EventHandleResult.success();
    }
}
