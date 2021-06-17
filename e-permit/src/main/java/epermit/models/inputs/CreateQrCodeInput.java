package epermit.models.inputs;

import lombok.Data;

@Data
public class CreateQrCodeInput {
    private String id;
    private String issuedFor;
    private String issuedAt;
    private String expireAt;
    private String plateNumber;
    private String companyName;
}
