package epermit.dtos;

import java.util.Date;
import epermit.common.PermitType;
import lombok.Data;

@Data
public class IssuerQuotaDto {

    private int id;

    private int year;

    private PermitType permitType;

    private int startId;

    private int currentId;

    private int endId;

    private Boolean active;
    
    private Date createdAt;

    private Date disabledAt;
}
