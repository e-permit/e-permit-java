package epermit.events;

import lombok.Data;

@Data
public class AppEvent {
    private String eventId;
    private String uri;
    private String jws;
}