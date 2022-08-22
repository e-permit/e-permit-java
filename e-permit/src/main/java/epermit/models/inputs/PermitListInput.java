package epermit.models.inputs;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitListInput {
    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("issued_for")
    private String issuedFor;

    @JsonProperty("issued_at")
    private String issuedAt;

    @JsonProperty("permit_year")
    private Integer permitYear;

    @JsonProperty("permit_type")
    private PermitType permitType;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("page")
    private Integer page = 0;

}
