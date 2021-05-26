package epermit.models.dtos;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class VerifierQuotaDto {

    private int id;

    private int year;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
