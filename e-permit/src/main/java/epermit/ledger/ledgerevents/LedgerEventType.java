package epermit.ledger.ledgerevents;


public enum LedgerEventType {
    PERMIT_CREATED("PERMIT_CREATED"), PERMIT_USED("PERMIT_USED"), PERMIT_REVOKED(
            "PERMIT_REVOKED"), RULE_CHANGED("RULE_CHANGED"), KEY_CREATED(
                    "KEY_CREATED"), KEY_REVOKED("KEY_REVOKED"), QUOTA_CREATED("QUOTA_CREATED");

    public final String ledgerEventType;

    private LedgerEventType(String ledgerEventType) {
        this.ledgerEventType = ledgerEventType;
    }
}
