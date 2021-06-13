package epermit.ledger.ledgerevents.keycreated;

import epermit.ledger.ledgerevents.LedgerEventBase;
import epermit.models.dtos.PublicJwk;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCreatedLedgerEvent extends LedgerEventBase {
    private PublicJwk jwk;
}
