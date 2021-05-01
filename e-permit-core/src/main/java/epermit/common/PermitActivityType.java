package epermit.common;

public enum PermitActivityType {
    ENTERANCE(1), QUIT(2);

    private Integer code;

    private PermitActivityType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
