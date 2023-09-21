package epermit.models.inputs;

import java.util.Optional;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserInput {
    @NotNull
    private String username;
    @NotNull
    private String role;
    private Optional<String> terminal;
}
