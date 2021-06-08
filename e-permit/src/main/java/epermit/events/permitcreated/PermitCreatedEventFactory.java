package epermit.events.permitcreated;

import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.utils.GsonUtil;
import epermit.entities.IssuedPermit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitCreatedEventFactory {
    private final EventFactoryUtil util;

    public PermitCreatedEvent create(IssuedPermit permit) {
        log.info("PermitCreatedEventFactory started with {}", permit);
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setEventType(EventType.PERMIT_CREATED);
        event.setCompanyName(permit.getCompanyName());
        event.setCompanyId(permit.getCompanyId());
        event.setExpireAt(permit.getExpireAt());
        event.setIssuedAt(permit.getIssuedAt());
        event.setPermitId(permit.getPermitId());
        event.setPermitType(permit.getPermitType());
        event.setPermitYear(permit.getPermitYear());
        event.setPlateNumber(permit.getPlateNumber());
        event.setSerialNumber(permit.getSerialNumber());
        if (permit.getClaims() != null && !permit.getClaims().isEmpty()) {
            event.setClaims(GsonUtil.toMap(permit.getClaims()));
        }
        log.info("PermitCreatedEventFactory ended with {}", event);
        util.saveAndPublish(event, permit.getIssuedFor());
        return event;
    }
}
