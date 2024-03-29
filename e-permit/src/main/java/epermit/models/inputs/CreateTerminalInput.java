package epermit.models.inputs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTerminalInput {
    @NotNull
    private String code;
    @NotNull
    private String name;
}
