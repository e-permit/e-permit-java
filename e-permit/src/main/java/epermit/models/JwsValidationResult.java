package epermit.models;

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

    public static JwsValidationResult fail(String errorCode){
        JwsValidationResult r = new JwsValidationResult();
        r.setValid(false);
        r.setErrorCode(errorCode);
        return r;
    }
}