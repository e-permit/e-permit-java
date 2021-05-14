package epermit.events.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service("PERMIT_REVOKED")
@RequiredArgsConstructor
public class PermitRevokedEventHandler implements EventHandler {
    private final PermitService permitService;
    @SneakyThrows
    public EventHandleResult handle(String payload) {
        PermitRevokedEvent event = GsonUtil.getGson().fromJson(payload, PermitRevokedEvent.class);      
        if(!permitService.isPermitExist(event.getIssuedFor(), event.getPermitId())){
            return EventHandleResult.fail("INVALID_PERMITID_OR_ISSUER");
        }
        permitService.handlePermitRevoked(event);
        return EventHandleResult.success();
    }
}
