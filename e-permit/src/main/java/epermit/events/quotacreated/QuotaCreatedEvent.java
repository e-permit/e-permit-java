package epermit.events.quotacreated;
import epermit.events.EventBase;
import epermit.models.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotaCreatedEvent extends EventBase {

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
