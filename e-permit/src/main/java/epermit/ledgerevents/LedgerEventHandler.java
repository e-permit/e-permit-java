package epermit.ledgerevents;

import java.util.Map;

public interface LedgerEventHandler {
    void handle(Map<String, Object> event);
}
