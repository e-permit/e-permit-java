package epermit.models.inputs;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CreateAuthorityInput {

    @NotBlank
    @Size(max = 1000)
    @Schema(name = "public_api_uri", description = "The public api url of authority", example = "https://example.gov" )
    private String publicApiUri;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Authority code, for countries it is two letter country code", example = "A" )
    private String code;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Authority name", example = "CountryA" )
    private String name;
}
