package epermit.ledger.models.enums;

public enum PermitActivityType {
    ENTERANCE("ENTERANCE"), EXIT("EXIT");
    public final String activityType;

    private PermitActivityType(String activityType) {
        this.activityType = activityType;
    }
}
