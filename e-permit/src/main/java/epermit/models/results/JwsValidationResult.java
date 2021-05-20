package epermit.models.results;

import java.util.Map;
import lombok.Data;

@Data
public class JwsValidationResult {
    private boolean valid;
    private String errorCode;
    private Map<String, Object> payload;

    public static JwsValidationResult success(Map<String, Object> payload){
        JwsValidationResult r = new JwsValidationResult();
        r.setValid(true);
        r.setPayload(payload);
        return r;
    }

    public static JwsValidationResult fail(String errorCode){
        JwsValidationResult r = new JwsValidationResult();
        r.setValid(false);
        r.setErrorCode(errorCode);
        return r;
    }
}