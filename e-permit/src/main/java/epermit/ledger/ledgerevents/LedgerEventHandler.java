package epermit.ledger.ledgerevents;

public interface LedgerEventHandler {
    void handle(Object event);
}
