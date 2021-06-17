package epermit.models.valueobjects;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorityIssuerQuotaPayload {

    private Integer activeQuotaId;

    private Integer nextNumber;

    private List<Integer> usedQuotaIds;

    private List<Integer> availableSerialNumbers;
}
