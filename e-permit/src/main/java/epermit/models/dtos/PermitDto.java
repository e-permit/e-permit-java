package epermit.models.dtos;

import java.util.Date;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitDto {
    private Long id;
    
    private String permitId;

    private int serialNumber;

    private PermitType permitType;

    private int permitYear;

    private String issuedAt;

    private String expireAt;

    private String plateNumber;

    private String companyName;

    private String claims;

    private boolean used;

    private Date usedAt;

    private boolean revoked;

    private Date revokedAt;
}

