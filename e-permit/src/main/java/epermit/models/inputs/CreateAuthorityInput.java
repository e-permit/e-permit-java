package epermit.models.inputs;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateAuthorityInput {
    @NotBlank
    @URL
    private String apiUri;
}
