package epermit.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PermitType {
    BILITERAL("BILITERAL"), TRANSIT("TRANSIT"), THIRDCOUNTRY("THIRDCOUNTRY");

    private final String permitType;

    private PermitType(String permitType) {
        this.permitType = permitType;
    }

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
            default:
              code = "";
        }
        return code;
    }

    @JsonValue
    public String getValue() {
        return permitType;
    }
}
