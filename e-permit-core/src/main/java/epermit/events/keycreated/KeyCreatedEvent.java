package epermit.events.keycreated;

import epermit.events.EventBase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class KeyCreatedEvent extends EventBase {
    private String keyId;
    
    private String jwk;
}
