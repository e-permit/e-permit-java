package epermit.events;

/*import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimbusds.jose.JWSObject;

import epermit.common.EventType;
import epermit.common.JsonUtil;
import epermit.events.keycreated.KeyCreatedEvent;
import epermit.events.keycreated.KeyCreatedEventHandler;
import lombok.SneakyThrows;

public class EventResolver {
    @SneakyThrows
    public EventHandler getEvent(String jws) {
        EventHandler handler = null;
        JWSObject jwsObject = JWSObject.parse(jws);
        String payload = jwsObject.getPayload().toString();
        Gson gson = JsonUtil.getGson();
        EventType type = JsonUtil.getClaim(jws, "event_type");
        switch(type){
            case KEY_CREATED:
                KeyCreatedEvent event = gson.fromJson(payload, KeyCreatedEvent.class);
                handler = new KeyCreatedEventHandler();
            default:
                break;
              
        }
        return handler;
    } 
}*/
