package epermit.events.permitcreated;

import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import epermit.common.JsonUtil;
import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.entities.IssuedPermit;
import epermit.events.EventFactoryBase;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public class PermitCreatedEventFactory extends EventFactoryBase {
    public PermitCreatedEventFactory(PermitProperties props, CreatedEventRepository createdEventRepository,
            KeyService sJwsService) {
        super(props, createdEventRepository, sJwsService);
    }

    public CreatedEvent create(IssuedPermit permit) {
        Gson gson = JsonUtil.getGson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        PermitCreatedEvent e = PermitCreatedEvent.builder().companyName(permit.getCompanyName())
                .permitId(permit.getPermitId()).permitType(permit.getPermitType()).permitYear(permit.getPermitYear())
                .plateNumber(permit.getPlateNumber()).claims(gson.fromJson(permit.getClaims(), type))
                .serialNumber(permit.getSerialNumber()).build();
        setCommonClaims(e, permit.getIssuedFor(), EventType.PERMIT_CREATED);
        return persist(e);
    }
}
