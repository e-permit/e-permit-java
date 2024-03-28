package epermit.models.inputs;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateAuthorityInput {

    //private boolean xroad;

    @NotBlank
    private String publicApiUri;

    @NotBlank
    private String code;

    @NotBlank
    private String name;
}
