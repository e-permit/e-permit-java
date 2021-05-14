package epermit.services;

public interface EventService {
    String getReceivedLastEventId(String issuer);
    String getSendedLastEventId(String issuedFor);
    boolean isReceivedEventExist(String issuer, String eventId);
}
