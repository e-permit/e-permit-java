package epermit.models.enums;

public enum AuthenticationType {
    BASIC("BASIC"), BEARER("BEARER");
    public final String authenticationType;

    private AuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }
}