package epermit.events.permitcreated;

import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.IssuedPermit;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitCreatedEventFactory {
    private final EventFactoryUtil util;

    public PermitCreatedEvent create(IssuedPermit permit) {  
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setEventType(EventType.PERMIT_CREATED);
        event.setClaims(permit.getClaims());
        event.setCompanyName(permit.getCompanyName());
        event.setExpireAt(permit.getExpireAt());
        event.setIssuedAt(permit.getIssuedAt());
        event.setPermitId(permit.getPermitId());
        event.setPermitType(permit.getPermitType());
        event.setPermitYear(permit.getPermitYear());
        event.setPlateNumber(permit.getPlateNumber());
        event.setSerialNumber(permit.getSerialNumber());
        util.setCommon(event, "input");
        return event;
    }
}
