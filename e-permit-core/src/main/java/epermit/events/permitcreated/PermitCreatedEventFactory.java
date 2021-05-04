package epermit.events.permitcreated;

import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import epermit.common.EventType;
import epermit.common.JsonUtil;
import epermit.entities.IssuedPermit;

public class PermitCreatedEventFactory {
    public PermitCreatedEvent create(IssuedPermit permit) {
        Gson gson = JsonUtil.getGson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        PermitCreatedEvent e = PermitCreatedEvent.builder().companyName(permit.getCompanyName())
                .serialNumber(permit.getSerialNumber()).permitType(permit.getPermitType())
                .permitYear(permit.getPermitYear()).plateNumber(permit.getPlateNumber())
                .claims(gson.fromJson(permit.getClaims(), type)).permitId(permit.getPermitId()).build();
        e.setEventType(EventType.PERMIT_CREATED);
        return e;
    }
}
