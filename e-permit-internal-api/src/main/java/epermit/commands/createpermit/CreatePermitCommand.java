package epermit.commands.createpermit;

import java.util.HashMap;
import java.util.Map;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.common.PermitType;
import lombok.Data;

@Data
public class CreatePermitCommand implements Command<CommandResult> {
    private String issuedFor;

    private PermitType permitType;

    private int permitYear;

    private String plateNumber;

    private String companyName;

    private Map<String, Object> claims = new HashMap<>();
}
