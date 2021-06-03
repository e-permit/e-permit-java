package epermit.events.permitused;

import org.springframework.stereotype.Service;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuedPermitActivity;
import epermit.events.EventHandler;
import epermit.repositories.IssuedPermitRepository;
import lombok.RequiredArgsConstructor;

@Service("PERMIT_USED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitUsedEventHandler implements EventHandler {

    private final IssuedPermitRepository issuedPermitRepository;

    @Override
    public void handle(Object e) {
        PermitUsedEvent event = (PermitUsedEvent) e;
        IssuedPermit permit = issuedPermitRepository
                .findOneByIssuedForAndPermitId(event.getIssuedFor(), event.getPermitId()).get();
        permit.setUsed(true);
        IssuedPermitActivity activity = new IssuedPermitActivity();
        activity.setActivityType(event.getActivityType());
        activity.setActivityTimestamp(event.getActivityTimestamp());
        permit.addActivity(activity);
        issuedPermitRepository.save(permit);
    }
}
