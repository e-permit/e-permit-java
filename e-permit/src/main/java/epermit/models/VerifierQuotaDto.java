package epermit.models;

import java.util.Date;
import lombok.Data;

@Data
public class VerifierQuotaDto {

    private int id;

    private int year;

    private PermitType permitType;

    private int startId;

    private int endId;

    private Date createdAt;
}
