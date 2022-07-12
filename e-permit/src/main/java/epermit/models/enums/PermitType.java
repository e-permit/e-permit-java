package epermit.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.SneakyThrows;

public enum PermitType {
    BILITERAL("BILITERAL"), TRANSIT("TRANSIT"), THIRDCOUNTRY("THIRDCOUNTRY"), BILITERAL_FEE(
            "BILITERAL_FEE"), TRANSIT_FEE("TRANSIT_FEE"), THIRDCOUNTRY_FEE("THIRDCOUNTRY_FEE");

    private final String permitType;

    private PermitType(String permitType) {
        this.permitType = permitType;
    }

    @SneakyThrows
    public String getCode() {
        String code;
        switch (permitType) {
            case "BILITERAL":
                code = "1";
                break;
            case "TRANSIT":
                code = "2";
                break;
            case "THIRDCOUNTRY":
                code = "3";
                break;
            case "BILITERAL_FEE":
                code = "4";
                break;
            case "TRANSIT_FEE":
                code = "5";
                break;
            case "THIRDCOUNTRY_FEE":
                code = "6";
                break;
            default:
                throw new Exception("Unknown permit code");
        }
        return code;
    }

    @JsonValue
    public String getValue() {
        return permitType;
    }
}
