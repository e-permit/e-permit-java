package epermit.models.dtos;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PermitListItem {
    private UUID id;

    private String issuer;

    private String issuedFor;

    private String permitId;

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

    @Schema(description = "This field is set to true if the permit is revoked")
    private boolean revoked;

    private Long revokedAt;

    @Schema(description = "This field is set to true if the permit is used once")
    private boolean used;
}
