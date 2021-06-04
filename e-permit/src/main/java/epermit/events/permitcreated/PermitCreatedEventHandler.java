package epermit.events.permitcreated;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.entities.Permit;
import epermit.events.EventHandler;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("PERMIT_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitCreatedEventHandler implements EventHandler {
    private final PermitRepository permitRepository;

    public void handle(Object e) {
        PermitCreatedEvent event = (PermitCreatedEvent)e;
        Gson gson = GsonUtil.getGson();
        Permit permit = new Permit();
        if (event.getClaims() != null && !event.getClaims().isEmpty()) {
            permit.setClaims(gson.toJson(event.getClaims()));
        }
        permit.setCompanyName(event.getCompanyName());
        permit.setExpireAt(event.getExpireAt());
        permit.setIssuedAt(event.getIssuedAt());
        permit.setIssuer(event.getIssuer());
        permit.setPermitId(event.getPermitId());
        permit.setPermitType(event.getPermitType());
        permit.setPermitYear(event.getPermitYear());
        permit.setPlateNumber(event.getPlateNumber());
        permit.setSerialNumber(event.getSerialNumber());
        permitRepository.save(permit);
    }
}
