package epermit.models;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class IssuedPermit {
    private String issuedFor;
    private String issuedAt;
    private String expireAt;
    private String permitId;
    private Integer serialNumber;
    private String qrCode;
    private PermitType permitType;
    private int permitYear;
    private String plateNumber;
    private String companyName;
    private Map<String, Object> claims = new HashMap<>();
}
