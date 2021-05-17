package epermit.models;

import java.util.Date;
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
