package epermit.ledger.contracts;

import lombok.Data;

@Data
public class ContractEvent {
    private String uri;
    private String jws;
}

