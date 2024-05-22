package epermit.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitListParams {
    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("issued_for")
    private String issuedFor;

    @JsonProperty("permit_year")
    private Integer permitYear;

    @JsonProperty("permit_type")
    private PermitType permitType;

    @JsonProperty("used")
    private Boolean used;
}
