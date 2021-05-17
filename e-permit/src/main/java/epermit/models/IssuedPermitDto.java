package epermit.models;

import java.util.Date;
import lombok.Data;

@Data
public class IssuedPermitDto {
    private Long id;

    private String permitId;

    private String qrCode;

    private int serialNumber;

    private PermitType permitType;

    private int permitYear;

    private long issuedAt;

    private String plateNumber;

    private String companyName;

    private String claims;

    private boolean used;

    private Date usedAt;

    private boolean revoked;

    private Date revokedAt;
}
