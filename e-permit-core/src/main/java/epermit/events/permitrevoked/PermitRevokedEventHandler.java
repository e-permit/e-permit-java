package epermit.events.permitrevoked;

import java.util.Optional;
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

    public PermitRevokedEventHandler(PermitRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    public EventHandleResult handle(String payload) {
        PermitRevokedEvent event = JsonUtil.getGson().fromJson(payload, PermitRevokedEvent.class);      
        Optional<Permit> permitR = repository.findOneByPermitId(event.getPermitId());
        if (!permitR.isPresent()) {
            return EventHandleResult.fail("INVALID_PERMITID");
        }
        Permit permit = permitR.get();
        if(!permit.getIssuer().equals(event.getIssuer())){
            return EventHandleResult.fail("INVALID_PERMIT_ISSUER");
        }
        repository.delete(permit);
        return EventHandleResult.success();
    }
}
