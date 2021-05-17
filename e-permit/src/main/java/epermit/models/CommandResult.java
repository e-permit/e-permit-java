package epermit.models;


import java.time.OffsetDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class CommandResult {

    private boolean ok;

    private String error;

    private OffsetDateTime date;

    private Map<String, String> result;

    public static CommandResult success() {
        CommandResult cr = new CommandResult();
        cr.date = OffsetDateTime.now();
        cr.setOk(true);
        return cr;
    }

    public static CommandResult fail(String error) {
        CommandResult cr = new CommandResult();
        cr.date = OffsetDateTime.now();
        cr.setOk(false);
        return cr;
    }
}


