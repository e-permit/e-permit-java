package epermit.models.inputs;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateAuthorityInput {

    @NotBlank
    @Schema(name = "public_api_uri", description = "The public api url of authority", example = "https://example.gov" )
    private String publicApiUri;

    @NotBlank
    @Schema(description = "Authority code, for countries it is two letter country code", example = "A" )
    private String code;

    @NotBlank
    @Schema(description = "Authority name", example = "CountryA" )
    private String name;
}
