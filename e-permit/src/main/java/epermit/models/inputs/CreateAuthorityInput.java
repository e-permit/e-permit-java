package epermit.models.inputs;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateAuthorityInput {

    @NotBlank
    @Schema(description = "The public api url of authority", example = "https://example.gov" )
    private String publicApiUri;

    @NotBlank
    @Schema(description = "Authority code, for countries it is two letter country code", example = "TR" )
    private String code;

    @NotBlank
    @Schema(description = "Authority name", example = "TÜRKİYE" )
    private String name;
}
