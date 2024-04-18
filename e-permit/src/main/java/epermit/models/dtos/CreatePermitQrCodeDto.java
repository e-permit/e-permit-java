package epermit.models.dtos;

import lombok.Data;

@Data
public class CreatePermitQrCodeDto {
    private String id;
    private String issuedAt;
    private String expireAt;
    private String plateNumber;
    private String companyName;
}