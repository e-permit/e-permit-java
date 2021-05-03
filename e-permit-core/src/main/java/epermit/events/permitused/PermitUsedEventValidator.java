package epermit.events.permitused;

import org.springframework.stereotype.Component;

@Component
public class PermitUsedEventValidator {
    public Boolean validate(PermitUsedEvent e){
        return true;
    }
}
