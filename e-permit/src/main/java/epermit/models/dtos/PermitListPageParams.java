package epermit.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class PermitListPageParams {
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
    private String createdAt;

    @JsonProperty("page")
    private Integer page = 0;

}
