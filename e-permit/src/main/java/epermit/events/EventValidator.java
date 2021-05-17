package epermit.events;

public interface EventValidator {
    EventValidationResult validate(String payload);
}
