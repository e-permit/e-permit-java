package epermit.models.enums;

public enum IssuerQuotaSerialNumberState {
    CREATED("C"), USED("U"), REVOKED("R");
    public final String status;

    private IssuerQuotaSerialNumberState(String status) {
        this.status = status;
    }
}