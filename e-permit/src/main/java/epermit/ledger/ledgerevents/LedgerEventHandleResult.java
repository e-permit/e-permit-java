package epermit.ledger.ledgerevents;

import lombok.Data;

@Data
public class LedgerEventHandleResult {

    private boolean ok;

    private String errorCode;

    public static LedgerEventHandleResult fail(String errorCode){
        LedgerEventHandleResult r = new LedgerEventHandleResult();
        r.ok = false;
        r.errorCode = errorCode;
        return r;
    }

    public static LedgerEventHandleResult success(){
        LedgerEventHandleResult r = new LedgerEventHandleResult();
        r.ok = true;
        return r;
    }
}

