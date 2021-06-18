package epermit.ledgerevents.permitcreated;

import java.util.Map;
import javax.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import epermit.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCreatedLedgerEvent extends LedgerEventBase {
    public PermitCreatedLedgerEvent(String issuer, String issuedFor, String prevEventId) {
        super(issuer, issuedFor, prevEventId, LedgerEventType.PERMIT_CREATED);
    }
    @NotNull
    private String permitId;

    @NotNull
    private PermitType permitType;

    @NotNull
    private int permitYear;

    @NotNull
    private int serialNumber;

    @NotNull
    private String IssuedAt;

    @NotNull
    private String ExpireAt;

    @NotNull
    private String companyName;

    @NotNull
    private String companyId;

    @NotNull
    private String plateNumber;

    private Map<String, Object> claims;
}