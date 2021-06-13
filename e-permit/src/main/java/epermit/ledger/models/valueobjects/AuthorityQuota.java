package epermit.ledger.models.valueobjects;

import java.util.List;
import epermit.ledger.models.enums.PermitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorityQuota {
    private int nextNumber;

    private PermitType permitType;

    private int permitYear;

    private List<String> revokedPermits;
}
