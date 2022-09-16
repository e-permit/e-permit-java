package epermit.appevents;

import java.util.Map;
import lombok.Data;

@Data
public class LedgerEventCreated {
    private String uri;
    private Map<String, Object> content;
    private String proof;
    private String eventId;
}
