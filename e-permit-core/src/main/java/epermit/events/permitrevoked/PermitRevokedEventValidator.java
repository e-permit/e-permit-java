package epermit.events.permitrevoked;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PermitRevokedEventValidator {
    public Boolean validate(PermitRevokedEvent e) {
        return true;
    }
}