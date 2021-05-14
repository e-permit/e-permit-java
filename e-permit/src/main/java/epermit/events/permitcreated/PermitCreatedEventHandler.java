package epermit.events.permitcreated;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.services.AuthorityService;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;
import epermit.utils.PermitUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED")
@RequiredArgsConstructor
public class PermitCreatedEventHandler implements EventHandler {
    private final PermitService permitService;
    private final AuthorityService authorityService;
    private final PermitUtil permitUtil;
    @SneakyThrows
    public EventHandleResult handle(String payload) {
        Gson gson = GsonUtil.getGson();
        PermitCreatedEvent event = gson.fromJson(payload, PermitCreatedEvent.class);
        EventHandleResult validation = validate(event);
        if (!validation.isOk()) {
            return validation;
        }
        permitService.handlePermitCreated(event);
        return EventHandleResult.success();
    }

    private EventHandleResult validate(PermitCreatedEvent event) {
        String expectedPermitId = permitUtil.getPermitId(event.getIssuer(), event.getIssuedFor(),
                event.getPermitType(), event.getPermitYear(), event.getSerialNumber());
        if (!expectedPermitId.equals(event.getPermitId())) {
            log.info("INVALID_PERMITID");
            return EventHandleResult.fail("INVALID_PERMITID");
        }
        boolean exist = permitService.isIssuedPermitExist(event.getIssuedFor(), event.getPermitId());
        if (exist) {
            log.info("PERMIT_EXIST");
            return EventHandleResult.fail("PERMIT_EXIST");
        }
        
        if (!authorityService.isQuotaSufficient()) {
            log.info("QUOTA_DOESNT_MATCH");
            return EventHandleResult.fail("QUOTA_DOESNT_MATCH");
        }
        return EventHandleResult.success();
    }
}
