package epermit.events.permitused;
import epermit.events.EventBase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PermitUsedEvent extends EventBase {
    private String serialNumber;
}
