package epermit.appevents;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class QuotaCreated {
    private String permitIssuedFor;

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
