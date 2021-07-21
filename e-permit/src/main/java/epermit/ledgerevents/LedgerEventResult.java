package epermit.ledgerevents;

import lombok.Data;

@Data
public class LedgerEventResult {
    private boolean ok;

    private String errorCode;

    private String errorMessage;

    public static LedgerEventResult fail(String errorCode, String errorMessage){
        LedgerEventResult r = new LedgerEventResult();
        r.ok = false;
        r.errorCode = errorCode;
        r.errorMessage = errorMessage;
        return r;
    }

    public static LedgerEventResult success(){
        LedgerEventResult r = new LedgerEventResult();
        r.ok = true;
        return r;
    }
}
