package epermit.ledger.models.inputs;

import lombok.Data;

@Data
public class CreateQrCodeInput {
    private String id;
    private String issuedAt;
    private String expireAt;
    private String plateNumber;
    private String companyName;
}
