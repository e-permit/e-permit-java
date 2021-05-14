package epermit.events;

import lombok.Data;

@Data
public class EventHandleResult {

    private boolean ok;

    private String errorCode;

    private Object result;

    public static EventHandleResult fail(String errorCode){
        EventHandleResult r = new EventHandleResult();
        r.ok = false;
        r.errorCode = errorCode;
        return r;
    }

    public static EventHandleResult success(){
        EventHandleResult r = new EventHandleResult();
        r.ok = true;
        return r;
    }

    public static EventHandleResult success(Object result){
        EventHandleResult r = new EventHandleResult();
        r.result = result;
        r.ok = true;
        return r;
    }
}
