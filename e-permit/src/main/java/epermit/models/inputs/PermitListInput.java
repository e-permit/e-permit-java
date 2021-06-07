package epermit.models.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitListInput {
    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("permit_year")
    private Integer permitYear;

    @JsonProperty("permit_type")
    private PermitType permitType;

    @JsonProperty("page")
    private Integer page = 0;
}