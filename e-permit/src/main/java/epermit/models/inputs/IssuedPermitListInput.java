package epermit.models.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class IssuedPermitListInput {
    @JsonProperty("issued_for")
    private String issuedFor;

    @JsonProperty("permit_year")
    private Integer permitYear;

    @JsonProperty("permit_type")
    private PermitType permitType;

    @JsonProperty("company_id")
    private String companyId;

    @JsonProperty("page")
    private Integer page = 0;
}
