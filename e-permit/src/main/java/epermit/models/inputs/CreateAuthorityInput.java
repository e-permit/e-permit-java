package epermit.models.inputs;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateAuthorityInput {
    @NotBlank
    @URL
    private String apiUri;
}
