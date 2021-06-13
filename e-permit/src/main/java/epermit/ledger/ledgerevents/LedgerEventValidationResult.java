package epermit.ledger.ledgerevents;

import lombok.Data;

@Data
public class LedgerEventValidationResult {

    private boolean ok;

    private String errorCode;

    private Object event;

    public static LedgerEventValidationResult fail(String errorCode, Object event){
        LedgerEventValidationResult r = new LedgerEventValidationResult();
        r.ok = false;
        r.errorCode = errorCode;
        r.event = event;
        return r;
    }

    public static LedgerEventValidationResult jwsFail(String errorCode){
        LedgerEventValidationResult r = new LedgerEventValidationResult();
        r.ok = false;
        r.errorCode = errorCode;
        return r;
    }

    public static LedgerEventValidationResult success(Object event){
        LedgerEventValidationResult r = new LedgerEventValidationResult();
        r.event = event;
        r.ok = true;
        return r;
    }
}

