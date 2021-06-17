package epermit.ledgerevents;

public interface LedgerEventHandler {
    LedgerEventHandleResult handle(Object event);
}
