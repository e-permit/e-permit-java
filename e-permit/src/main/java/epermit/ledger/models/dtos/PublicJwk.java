package epermit.ledger.models.dtos;

import lombok.Data;

@Data
public class PublicJwk {
    private String kty;
    private String use;
    private String crv;
    private String kid;
    private String x;
    private String y;
    private String alg;
}

