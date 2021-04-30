package epermit.events.quotacreated;
import epermit.common.PermitType;
import epermit.events.EventBase;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class QuotaCreatedEvent extends EventBase {

    private int permitYear;

    private PermitType permitType;

    private int startId;

    private int endId;
}
