package epermit.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class SpringEventHandlerRegistry implements ApplicationListener<ApplicationReadyEvent> {
 
    private final List<EventHandlerProxy> eventHandlerProxies = new ArrayList<>();
 
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName: beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Assert.state(parameterTypes.length == 1, "Event handler must have exactly one parameter!");
                    Class<?> parameterType = parameterTypes[0]; // one and only!
                    EventHandlerProxy handlerProxy = new EventHandlerProxy(parameterType, bean, method);
                    eventHandlerProxies.add(handlerProxy);
                }
            }
        }
    }
    
    public List<EventHandlerProxy> getEventHandlers(String acceptedType) {
        return eventHandlerProxies
                .stream()
                .filter(eventHandler -> eventHandler.accepts(acceptedType))
                .collect(Collectors.toList());
    }
}