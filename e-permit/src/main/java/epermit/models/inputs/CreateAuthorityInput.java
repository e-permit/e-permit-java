package epermit.models.inputs;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateAuthorityInput {
    @NotBlank
    @Size(min = 2, max = 2)
    private String code;

    @NotBlank
    @URL
    private String apiUri;

    @NotBlank
    private String Name;
}
