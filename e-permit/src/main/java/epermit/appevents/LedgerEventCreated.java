package epermit.appevents;

import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class LedgerEventCreated {
    private UUID id;
    private String url;
    private Map<String, Object> content;
    private String eventId;
}
