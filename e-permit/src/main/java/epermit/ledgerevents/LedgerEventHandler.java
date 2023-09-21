package epermit.ledgerevents;

public interface LedgerEventHandler {
    <T extends LedgerEventBase> void handle(T event);
}
