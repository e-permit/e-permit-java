package epermit.events.permitused;

import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;

@Service("PERMIT_USED")
public class PermitUsedEventHandler implements EventHandler {
    private final PermitUsedEventValidator validator;

    public PermitUsedEventHandler(PermitUsedEventValidator validator) {
        this.validator = validator;
    }

    @Override
    public EventHandleResult handle(String payload) {
        PermitUsedEvent event = JsonUtil.getGson().fromJson(payload, PermitUsedEvent.class);
        Boolean valid = validator.validate(event);
        if (!valid) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
        return EventHandleResult.success();
    }

}
