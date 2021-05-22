package epermit.events;

import java.util.Map;

public interface EventValidator {
    EventValidationResult validate(Map<String, Object> claims);
}
