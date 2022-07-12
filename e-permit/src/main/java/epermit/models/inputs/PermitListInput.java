package epermit.models.inputs;

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

    @JsonProperty("page")
    private Integer page = 0;

}
