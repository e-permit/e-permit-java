package epermit.models.dtos;

import lombok.Data;

@Data
public class PermitListPageParams {
    private String issuer;

    private String issuedFor;

    private Integer permitYear;

    private Integer permitType;

    private String issuedAt;

    private String createdAt;

    private Integer page = 0;

}
