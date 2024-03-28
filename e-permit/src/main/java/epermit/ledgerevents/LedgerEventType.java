package epermit.ledgerevents;

public enum LedgerEventType {
    PERMIT_CREATED("PERMIT_CREATED"), PERMIT_USED("PERMIT_USED"), PERMIT_REVOKED(
            "PERMIT_REVOKED"),
    QUOTA_CREATED("QUOTA_CREATED"), KEY_CREATED("KEY_CREATED"), KEY_REVOKED("KEY_REVOKED");

    public final String ledgerEventType;

    private LedgerEventType(String ledgerEventType) {
        this.ledgerEventType = ledgerEventType;
    }
}
