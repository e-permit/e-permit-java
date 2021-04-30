package epermit.commands.permitused;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import lombok.Data;

@Data
public class PermitUsedCommand implements Command<CommandResult> {
    private Long permitId;
}
