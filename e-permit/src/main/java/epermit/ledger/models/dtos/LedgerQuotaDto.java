package epermit.ledger.models.dtos;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class LedgerQuotaDto {

    private int id;

    private int year;

    private PermitType permitType;

    private int startId;

    private int currentId;

    private int endId;
}
