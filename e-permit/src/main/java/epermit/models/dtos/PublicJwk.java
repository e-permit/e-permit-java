package epermit.models.dtos;

import com.nimbusds.jose.jwk.JWK;

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

    public static PublicJwk fromJwk(JWK jwk) {
        PublicJwk pjwk = new PublicJwk();
        pjwk.setCrv("P-256");
        pjwk.setAlg("ES256");
        pjwk.setKid(jwk.getKeyID());
        pjwk.setKty("EC");
        pjwk.setUse("sig");
        pjwk.setX(jwk.getRequiredParams().get("x").toString());
        pjwk.setY(jwk.getRequiredParams().get("y").toString());
        return pjwk;
    }
}

