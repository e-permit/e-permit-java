package epermit.commands.createauthority;

import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import lombok.Data;

@Data
public class CreateAuthorityCommand implements Command<CommandResult> {
    private String code;

    private String apiUri;

    private String verifyUri;

    private String Name;
}
