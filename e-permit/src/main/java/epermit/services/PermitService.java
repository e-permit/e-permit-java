package epermit.services;

import epermit.events.permitcreated.PermitCreatedEvent;
import epermit.events.permitrevoked.PermitRevokedEvent;
import epermit.events.permitused.PermitUsedEvent;
import epermit.models.PermitType;

public interface PermitService {
    boolean isIssuedPermitExist(String issuedFor, String permitId);
    boolean isPermitExist(String issuer, String permitId);
    Integer getSerialNumber(String issuedFor, int py, PermitType pt);
    void createPermit();
    void revokePermit();
    void usePermit();
    void handlePermitCreated(PermitCreatedEvent event);
    void handlePermitRevoked(PermitRevokedEvent event);
    void handlePermitUsed(PermitUsedEvent event);
}
