package epermit.events.permitused;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuedPermitActivity;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.IssuedPermitRepository;

@Service("PERMIT_USED")
public class PermitUsedEventHandler implements EventHandler {

    private final IssuedPermitRepository repository;

    public PermitUsedEventHandler(IssuedPermitRepository repository) {
        this.repository = repository;
    }

    @Override
    public EventHandleResult handle(String payload) {
        PermitUsedEvent event = JsonUtil.getGson().fromJson(payload, PermitUsedEvent.class);
        Optional<IssuedPermit> permitR = repository.findOneByPermitId(event.getPermitId());
        if (!permitR.isPresent()) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
        IssuedPermit permit = permitR.get();
        if (permit.getIssuedFor().equals(event.getIssuer())) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
        permit.setUsed(true);
        IssuedPermitActivity activity = new IssuedPermitActivity();
        activity.setActivityType(event.getActivityType());
        activity.setCreatedAt(Instant.ofEpochSecond(event.getCreatedAt()).atOffset(ZoneOffset.UTC));
        permit.addActivity(activity);
        return EventHandleResult.success();
    }
}
