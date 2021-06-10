package epermit.ledger.contracts;


import lombok.Data;

@Data
public class ContractBase {
    private String issuer;

    private String issuedFor;

    private Long contractDate;

    private ContractType contractType;

    private String contractId;

    private String previousContractId;
}
