package epermit.events.keycreated;

import epermit.events.EventBase;
import epermit.models.dtos.PublicJwk;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCreatedEvent extends EventBase {
    private PublicJwk jwk;
}
