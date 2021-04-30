package epermit.events.permitcreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.google.gson.Gson;

import epermit.entities.Permit;
import epermit.events.EventHandleResult;
import epermit.repositories.PermitRepository;
import lombok.SneakyThrows;

public class PermitCreatedEventHandler {

    private final PermitRepository repository;
    private final PermitCreatedEventValidator validator;

    public PermitCreatedEventHandler(PermitRepository repository, PermitCreatedEventValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @SneakyThrows
    public EventHandleResult handle(PermitCreatedEvent event) {
        EventHandleResult r = validator.validate(event);
        if (!r.isSucceed())
            return r;
        Permit p = new Permit();
        Gson gson = new Gson();
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
