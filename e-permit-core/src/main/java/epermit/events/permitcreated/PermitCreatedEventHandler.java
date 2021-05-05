package epermit.events.permitcreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.entities.Permit;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.PermitRepository;
import lombok.SneakyThrows;

@Service("PERMIT_CREATED")
public class PermitCreatedEventHandler implements EventHandler {

    private final PermitRepository repository;
    private final PermitCreatedEventValidator validator;

    public PermitCreatedEventHandler(PermitRepository repository,
            PermitCreatedEventValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @SneakyThrows
    public EventHandleResult handle(String payload) {
        PermitCreatedEvent event = JsonUtil.getGson().fromJson(payload, PermitCreatedEvent.class);
        Boolean valid = validator.validate(event);
        if (!valid) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
        Gson gson = JsonUtil.getGson();
        Permit permit = new Permit();
        permit.setClaims(gson.toJson(event.getClaims()));
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
}
