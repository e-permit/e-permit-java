package epermit.ledgerevents.keycreated;

import epermit.ledgerevents.LedgerEventBase;
import epermit.models.dtos.PublicJwk;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCreatedLedgerEvent extends LedgerEventBase {
    private PublicJwk jwk;
}
