package epermit.events.permitrevoked;

import epermit.events.EventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitRevokedEvent extends EventBase {
    private String permitId;
}
