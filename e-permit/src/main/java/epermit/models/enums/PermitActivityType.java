package epermit.models.enums;

public enum PermitActivityType {
    ENTRANCE("ENTRANCE"), EXIT("EXIT");
    public final String activityType;

    private PermitActivityType(String activityType) {
        this.activityType = activityType;
    }
}
