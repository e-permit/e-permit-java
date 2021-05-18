package epermit.models.dtos;

import lombok.Data;

@Data
public class PublicKey {
    private String keyId;
    private PublicJwk jwk;
    private Long validFrom;
    private Long validUntil;
}

