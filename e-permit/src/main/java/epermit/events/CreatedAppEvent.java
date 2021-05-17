package epermit.events;

import lombok.Data;

@Data
public class CreatedAppEvent {
    private String uri;
    private String jws;
}
