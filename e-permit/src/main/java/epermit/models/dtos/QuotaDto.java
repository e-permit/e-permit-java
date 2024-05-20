package epermit.models.dtos;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class QuotaDto {

    private String permitIssuer;

    private String permitIssuedFor;

    private int permitYear;

    private PermitType permitType;

    private Long balance;

    private Long nextSerial;
}
