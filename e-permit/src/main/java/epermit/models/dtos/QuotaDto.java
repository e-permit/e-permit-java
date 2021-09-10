package epermit.models.dtos;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class QuotaDto {

    private int id;

    private String permitIssuer;

    private String permitIssuedFor;

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
