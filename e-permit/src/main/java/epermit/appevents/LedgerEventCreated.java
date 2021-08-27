package epermit.appevents;

import java.util.Map;
import epermit.models.enums.AuthenticationType;
import lombok.Data;

@Data
public class LedgerEventCreated {
    private String uri;
    private Map<String, Object> content;
    private AuthenticationType proofType;
    private String proof;
    private String eventId;
}
