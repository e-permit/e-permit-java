package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitDto {
    private UUID id;

    private String issuer;

    private String issuedFor;

    private String permitId;

    private String qrCode;

    private PermitType permitType;

    private int permitYear;

    private String issuedAt;

    private String expireAt;

    private String plateNumber;

    private String companyName;

    private String companyId;
    
    private String departureCountry;
    
    private String arrivalCountry;

    private String claims;

    private boolean used;

    private List<PermitActivityDto> activities = new ArrayList<>();
}
