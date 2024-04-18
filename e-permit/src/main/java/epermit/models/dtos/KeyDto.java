package epermit.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KeyDto {
    @NotBlank
    private String keyId;

    @NotBlank
    private String jwk;

    @NotBlank
    private String salt;

    @NotBlank
    private String privateJwk;
}
