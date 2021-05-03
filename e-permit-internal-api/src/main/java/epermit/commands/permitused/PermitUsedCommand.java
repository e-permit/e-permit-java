package epermit.commands.permitused;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.common.PermitActivityType;
import lombok.Data;

@Data
public class PermitUsedCommand implements Command<CommandResult> {
    private String permitId;

    private PermitActivityType activityType;
}
