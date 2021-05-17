package epermit.events;

import lombok.Data;

@Data
public class EventValidationResult {

    private boolean ok;

    private String errorCode;

    private Object event;

    public static EventValidationResult fail(String errorCode){
        EventValidationResult r = new EventValidationResult();
        r.ok = false;
        r.errorCode = errorCode;
        return r;
    }

    public static EventValidationResult success(Object event){
        EventValidationResult r = new EventValidationResult();
        r.event = event;
        r.ok = true;
        return r;
    }
}
