package epermit.ledgerevents.keycreated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCreatedLedgerEvent extends LedgerEventBase {
    public KeyCreatedLedgerEvent(String issuer, String issuedFor, String prevEventId) {
        super(issuer, issuedFor, prevEventId, LedgerEventType.KEY_CREATED);
    }

    @NotNull
    @Pattern( regexp = "^EC$")
    private String kty;

    @NotNull
    @Pattern( regexp = "^sig$")
    private String use;

    @NotNull
    @Pattern( regexp = "^P-256$")
    private String crv;

    @NotNull
    private String kid;

    @NotNull
    private String x;

    @NotNull
    private String y;

    @NotNull
    @Pattern( regexp = "^ES256$")
    private String alg;
}
