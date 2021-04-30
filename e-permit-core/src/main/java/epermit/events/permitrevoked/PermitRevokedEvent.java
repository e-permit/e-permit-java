package epermit.events.permitrevoked;

import epermit.events.EventBase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermitRevokedEvent extends EventBase {
    private String serialNumber;
}
