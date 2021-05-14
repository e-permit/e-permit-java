package epermit.models;

import lombok.Data;

@Data
public class PrivateKey {
    private String keyId;
    private String privateJwk;
    private String publicJwk;
    private String salt;
}
