package epermit.events.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.entities.Permit;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.PermitRepository;
import lombok.SneakyThrows;

@Service("PERMIT_REVOKED")
public class PermitRevokedEventHandler implements EventHandler {
    private final PermitRepository repository;
    private final PermitRevokedEventValidator validator;

    public PermitRevokedEventHandler(PermitRepository repository,
            PermitRevokedEventValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @SneakyThrows
    public EventHandleResult handle(String payload) {
        PermitRevokedEvent event = JsonUtil.getGson().fromJson(payload, PermitRevokedEvent.class);
        Boolean valid = validator.validate(event);
        if (!valid) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
        Permit p = repository.findOneByPermitId(event.getPermitId()).get();
        repository.delete(p);
        return EventHandleResult.success();
    }
}
