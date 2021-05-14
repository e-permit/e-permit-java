package epermit.events.permitused;

import org.springframework.stereotype.Service;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("PERMIT_USED")
@RequiredArgsConstructor
public class PermitUsedEventHandler implements EventHandler {

    private final PermitService permitService;

    @Override
    public EventHandleResult handle(String payload) {
        PermitUsedEvent event = GsonUtil.getGson().fromJson(payload, PermitUsedEvent.class);
        if(!permitService.isIssuedPermitExist(event.getIssuer(), event.getPermitId())){
            return EventHandleResult.fail("INVALID_PERMITID_OR_ISSUER");
        }
        permitService.handlePermitUsed(event);
        return EventHandleResult.success();
    }
}
