package epermit.ledger.ledgerevents;

public interface LedgerEventHandler {
    LedgerEventHandleResult handle(Object event);
}
