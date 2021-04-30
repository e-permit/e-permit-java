package epermit.commands.enablequota;


import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import lombok.Data;

@Data
public class EnableQuotaCommand implements Command<CommandResult> {
    private Integer quotaId;
}
