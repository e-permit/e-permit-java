package epermit.events.permitused;

import epermit.events.EventBase;
import epermit.models.enums.PermitActivityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUsedEvent extends EventBase {
    private String permitId;

    private PermitActivityType activityType;
}
