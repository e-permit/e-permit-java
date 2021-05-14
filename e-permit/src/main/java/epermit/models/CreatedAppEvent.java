package epermit.models;

import lombok.Data;

@Data
public class CreatedAppEvent {
    private String uri;
    private String jws;
}
