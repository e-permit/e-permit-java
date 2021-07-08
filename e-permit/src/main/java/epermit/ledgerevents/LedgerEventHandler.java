package epermit.ledgerevents;

import java.util.Map;

public interface LedgerEventHandler {
    LedgerEventHandleResult handle(Map<String, Object> event);
}
