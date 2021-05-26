package epermit.events;

public enum EventType {
    PERMIT_CREATED("PERMIT_CREATED"), PERMIT_REVOKED("PERMIT_REVOKED"), PERMIT_USED(
            "PERMIT_USED"), KEY_CREATED("KEY_CREATED"), QUOTA_CREATED("QUOTA_CREATED");

    public final String eventType;

    private EventType(String eventType) {
        this.eventType = eventType;
    }
}
