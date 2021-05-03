package epermit.events;

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventHandlerProxy {
 
    private final Class eventClass;
    private final Object target;
    private final Method method;
 
    public String getType() {
        return eventClass.getSimpleName(); // e.g. "MyEvent" for de.tuhrig.MyEvent.java
    }
 
    public boolean accepts(String type) {
        return getType().equals(type);
    }
 
    public Class getEventClass() {
        return eventClass;
    }
 
    public void invoke(Object event) {
        ReflectionUtils.invokeMethod(method, target, event);
    }
}