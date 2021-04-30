package epermit.dtos;

import java.util.Date;
import epermit.common.PermitType;
import lombok.Data;

@Data
public class IssuedPermitDto {
    private Long id;

    private String serialNumber;

    private String qrCode;

    private int permitId;

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
