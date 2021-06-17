package epermit.ledgerevents;

import lombok.Data;

@Data
public class LedgerEventCreated {
    private String uri;
    private String jws;
}
