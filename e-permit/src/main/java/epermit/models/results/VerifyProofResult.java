package epermit.models.results;

import lombok.Data;

@Data
public class VerifyProofResult {
    private boolean valid;
    private String proof;
    private String errorCode;

    public static VerifyProofResult success(String proof){
        VerifyProofResult r = new VerifyProofResult();
        r.setValid(true);
        r.setProof(proof);
        return r;
    }

    public static VerifyProofResult fail(String errorCode){
        VerifyProofResult r = new VerifyProofResult();
        r.setValid(false);
        r.setErrorCode(errorCode);
        return r;
    }
}
