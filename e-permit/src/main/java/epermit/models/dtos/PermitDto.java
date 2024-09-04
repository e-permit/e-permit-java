package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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

    private String expiresAt;

    private String plateNumber;

    private String plateNumber2;

    private String companyName;

    private String companyId;
    
    private String departureCountry;
    
    private String arrivalCountry;

    private String claims;

    @Schema(description = "This field is set to true if the permit is used once")
    private boolean used;
    
    @Schema(description = "This field is set to true if the permit is revoked")
    private boolean revoked;

    private Long revokedAt;

    @Schema(description = "The activities about the permit e.g. ENTRANCE, EXIT")
    private List<PermitActivityDto> activities = new ArrayList<>();
}
