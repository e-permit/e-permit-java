package epermit.events;

import java.util.Map;
import lombok.Data;

@Data
public class ReceivedAppEvent {
    private Map<String, Object> claims;
}
