package epermit.models.enums;

public enum AuthenticationType {
    BASIC("BASIC"), PUBLICKEY("PUBLICKEY");
    public final String authenticationType;

    private AuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }
}