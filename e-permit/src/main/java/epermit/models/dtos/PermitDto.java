package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PermitDto {

    private String issuer;

    private String issuedFor;

    private String permitId;

    private String qrCode;

    private Integer permitType;

    private int permitYear;

    private String issuedAt;

    private String expireAt;

    private String plateNumber;

    private String plateNumber2;

    private String companyName;

    private String companyId;
    
    private String departureCountry;
    
    private String arrivalCountry;

    private String claims;

    private boolean used;

    private boolean revoked;
    
    private Long revokedAt;

    private List<PermitActivityDto> activities = new ArrayList<>();
}
