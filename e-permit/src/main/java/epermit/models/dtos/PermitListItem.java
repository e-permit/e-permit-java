package epermit.models.dtos;

import java.util.UUID;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitListItem {
    private UUID id;

    private String issuer;

    private String issuedFor;

    private String permitId;

    private String qrCode;

    private int serialNumber;

    private PermitType permitType;

    private int permitYear;

    private String issuedAt;

    private String expireAt;

    private String plateNumber;

    private String companyName;

    private String companyId;

    private String claims;

    private boolean used;
}
