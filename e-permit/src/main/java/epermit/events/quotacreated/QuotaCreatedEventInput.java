package epermit.events.quotacreated;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class QuotaCreatedEventInput {
    private String issuedFor;
    private int permitYear;
    private PermitType permitType;
    private int startNumber;
    private int endNumber;
}
