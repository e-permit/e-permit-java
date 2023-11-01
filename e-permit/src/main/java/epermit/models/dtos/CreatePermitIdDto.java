package epermit.models.dtos;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class CreatePermitIdDto {
    private String issuer;
    private String issuedFor;
    private PermitType permitType;
    private int permitYear;
    private Long serialNumber;
}
