package epermit.models;

public enum PermitType {
    BILITERAL(1), TRANSIT(2), THIRDCOUNTRY(3);

    private Integer code;

    private PermitType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
