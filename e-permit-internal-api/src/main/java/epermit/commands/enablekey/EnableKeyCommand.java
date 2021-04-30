package epermit.commands.enablekey;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import lombok.Data;

@Data
public class EnableKeyCommand implements Command<CommandResult> {
    private String keyId;
}
