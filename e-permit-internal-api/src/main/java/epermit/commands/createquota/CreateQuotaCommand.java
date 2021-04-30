package epermit.commands.createquota;

import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.common.PermitType;
import lombok.Data;

@Data
public class CreateQuotaCommand implements Command<CommandResult> {
    private String authorityCode;

    private int permitYear;

    private PermitType permitType;

    private int startId;

    private int endId;
}
