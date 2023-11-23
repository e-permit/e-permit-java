package epermit.models.inputs;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateAuthorityInput {
    @NotBlank
    private String clientId;

    @NotBlank
    private String code;

    @NotBlank
    private String name;
}
