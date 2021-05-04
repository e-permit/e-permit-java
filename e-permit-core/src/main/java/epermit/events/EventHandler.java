package epermit.events;

public interface EventHandler {
    EventHandleResult handle(String jws);
}
