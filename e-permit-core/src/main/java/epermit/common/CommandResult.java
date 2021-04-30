package epermit.common;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class CommandResult {

    private boolean succeed;

    private String errorCode;

    private String errorMessage;

    private Map<String, String> props = new HashMap<>();

    public void addProp(String key, String value) {
        props.put(key, value);
    }

    public static CommandResult success() {
        CommandResult cr = new CommandResult();
        cr.setSucceed(true);
        return cr;
    }

    public static CommandResult fail(String errorCode, String errorMessage) {
        CommandResult cr = new CommandResult();
        cr.setSucceed(false);
        return cr;
    }
}

