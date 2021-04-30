package epermit.common;

import lombok.Data;

@Data
public class JwsValidationResult {
    private boolean valid;
    private String errorCode;

    public static JwsValidationResult success(){
        JwsValidationResult r = new JwsValidationResult();
        r.setValid(true);
        return r;
    }
}
