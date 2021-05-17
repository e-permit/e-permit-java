package epermit.events;

import lombok.Data;

@Data
public class ReceivedAppEvent {
    private String jws;
}
