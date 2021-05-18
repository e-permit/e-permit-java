package epermit.events.permitcreated;

import java.util.Map;
import epermit.events.EventBase;
import epermit.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCreatedEvent extends EventBase {
    private String permitId;
    private PermitType permitType;
    private int permitYear;
    private int serialNumber;
    private String IssuedAt;
    private String ExpireAt;
    private String companyName;
    private String plateNumber;
    private Map<String, Object> claims;
}
