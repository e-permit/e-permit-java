package epermit.events.keyrevoked;

import epermit.events.EventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevokedEvent extends EventBase {
    private String keyId;

    private Long revokedAt;
}
