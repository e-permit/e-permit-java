package epermit.models.dtos;

import java.util.Date;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class IssuerQuotaDto {

    private int id;

    private int year;

    private PermitType permitType;

    private int startId;

    private int currentId;

    private int endId;
}
