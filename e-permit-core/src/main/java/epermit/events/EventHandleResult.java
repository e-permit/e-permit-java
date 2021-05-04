package epermit.events;

import lombok.Data;

@Data
public class EventHandleResult {

    private Boolean succeed;

    private String errorCode;

    private String lastEventId;

    public static EventHandleResult fail(String errorCode){
        EventHandleResult r = new EventHandleResult();
        r.succeed = false;
        r.errorCode = errorCode;
        return r;
    }

    public static EventHandleResult success(){
        EventHandleResult r = new EventHandleResult();
        r.succeed = true;
        return r;
    }
}
