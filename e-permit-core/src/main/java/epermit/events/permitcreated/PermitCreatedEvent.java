package epermit.events.permitcreated;

import java.util.Map;
import epermit.common.PermitType;
import epermit.events.EventBase;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Getter
@Builder(toBuilder = true)
public class PermitCreatedEvent extends EventBase {
    private String permitId;
    private PermitType permitType;
    private int permitYear;
    private int serialNumber;
    private String IssuedAt;
    private String ExpireAt;
    private String companyName;
    private String plateNumber;
    @Singular
    private Map<String, Object> claims;
}
