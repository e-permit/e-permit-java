package epermit.common;

public enum EventState {
    NEW(1), LOCKED(2), HANDLED(3);

    private Integer code;

    private EventState(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
