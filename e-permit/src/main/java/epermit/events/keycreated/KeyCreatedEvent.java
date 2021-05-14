package epermit.events.keycreated;

import epermit.events.EventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCreatedEvent extends EventBase {
    private String keyId;

    private Long validFrom;
    
    private String jwk;
}
