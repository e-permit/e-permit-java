package epermit.models;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class CreatePermitInput {
    private String issuedFor;

    private PermitType permitType;

    private int permitYear;

    private String plateNumber;

    private String companyName;

    private Map<String, Object> claims = new HashMap<>();
}