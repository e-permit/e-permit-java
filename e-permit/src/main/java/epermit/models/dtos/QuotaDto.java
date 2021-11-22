package epermit.models.dtos;

import java.util.UUID;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class QuotaDto {

    private UUID id;

    private String permitIssuer;

    private String permitIssuedFor;

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
