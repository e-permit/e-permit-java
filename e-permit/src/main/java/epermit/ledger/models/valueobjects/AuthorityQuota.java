package epermit.ledger.models.valueobjects;

import java.util.List;
import epermit.ledger.models.enums.PermitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorityQuota {

    private Integer activeQuotaId;

    private PermitType permitType;

    private int permitYear;

    private int nextNumber;

    private List<Integer> usedQuotaIds;

    private List<Integer> serialNumbers;
}
