package epermit.ledger.contracts;


public enum ContractType {
    PERMIT_CREATED("PERMIT_CREATED"), PERMIT_USED("PERMIT_USED"), KEY_CREATED(
            "KEY_CREATED"), KEY_REVOKED("KEY_REVOKED"), QUOTA_CREATED("QUOTA_CREATED");

    public final String contractType;

    private ContractType(String contractType) {
        this.contractType = contractType;
    }
}
