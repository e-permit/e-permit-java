package epermit.common;

import lombok.Data;

@Data
public class JwsValidationResult {
    private boolean valid;
    private String issuer;
    private String errorCode;

    public static JwsValidationResult success(String issuer){
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
