package epermit.models.dtos;

import lombok.Data;

@Data
public class PermitListParams {
    private String issuer;

    private String issuedFor;

    private Integer permitYear;

    private Integer permitType;

    private Boolean used;
}
