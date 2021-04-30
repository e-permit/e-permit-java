package epermit.events;

import lombok.Getter;

@Getter
public class EventHandleResult {
    private boolean succeed;
    private String errorCode;

    public static EventHandleResult success(){
        EventHandleResult r = new EventHandleResult();
        r.succeed = true;
        return r;
    }

    public static EventHandleResult fail(String resultCode){
        EventHandleResult r = new EventHandleResult();
        r.succeed = false;
        r.errorCode = resultCode;
        return r;
    }
}
