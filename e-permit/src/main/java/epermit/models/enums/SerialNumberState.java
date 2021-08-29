package epermit.models.enums;

public enum SerialNumberState {
    CREATED("C"), USED("U"), REVOKED("R");
    public final String status;

    private SerialNumberState(String status) {
        this.status = status;
    }
}