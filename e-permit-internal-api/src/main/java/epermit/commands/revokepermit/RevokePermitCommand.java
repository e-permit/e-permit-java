package epermit.commands.revokepermit;

import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import lombok.Data;

@Data
public class RevokePermitCommand implements Command<CommandResult> {
    private Long permitId;

    private String comment;
}
