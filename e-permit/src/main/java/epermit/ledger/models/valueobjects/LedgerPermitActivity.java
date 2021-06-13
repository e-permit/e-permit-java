package epermit.ledger.models.valueobjects;

import epermit.ledger.models.enums.PermitActivityType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
public class LedgerPermitActivity {

    private PermitActivityType activityType;

    private Long activityTimestamp;
}
