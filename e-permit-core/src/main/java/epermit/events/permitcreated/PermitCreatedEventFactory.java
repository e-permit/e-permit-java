package epermit.events.permitcreated;

import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import epermit.common.EventType;
import epermit.common.JsonUtil;
import epermit.entities.IssuedPermit;
import epermit.events.EventFactoryUtil;

public class PermitCreatedEventFactory {
    private final EventFactoryUtil util;

    public PermitCreatedEventFactory(EventFactoryUtil util) {
        this.util = util;
    }

    public PermitCreatedEvent create(IssuedPermit permit) {
        Gson gson = JsonUtil.getGson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        PermitCreatedEvent e = PermitCreatedEvent.builder().companyName(permit.getCompanyName())
                .serialNumber(permit.getSerialNumber()).permitType(permit.getPermitType())
                .permitYear(permit.getPermitYear()).plateNumber(permit.getPlateNumber())
                .permitId(permit.getPermitId()).build();
        if (permit.getClaims() != null && !permit.getClaims().isEmpty()) {
            permit.setClaims(gson.fromJson(permit.getClaims(), type));
        }
        e.setEventType(EventType.PERMIT_CREATED);
        util.setCommon(e, permit.getIssuedFor());
        return e;
    }
}
