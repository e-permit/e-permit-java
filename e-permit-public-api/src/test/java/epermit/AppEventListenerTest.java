package epermit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventValidationResult;
import epermit.listeners.ReceivedAppEventListener;

@ExtendWith(MockitoExtension.class)
public class AppEventListenerTest {
    //@Mock EventService eventService;

    @Test
    void shouldNotCallGetEvents() {
        /*when(eventService.handle("jws")).thenReturn(EventValidationResult.success());
        ReceivedAppEventListener listener = new ReceivedAppEventListener(eventService);
        AppEvent event = new AppEvent();
        event.setJws("jws");
        listener.onAppEvent(event);
        verify(eventService, times(0)).getEvents(anyString());*/
    }

    @Test
    void shouldCallGetEventsIfNotExist() {
        /*when(eventService.handle("jws")).thenReturn(EventValidationResult.fail("NOTEXIST_PREVIOUSEVENT"));
        ReceivedAppEventListener listener = new ReceivedAppEventListener(eventService);
        AppEvent event = new AppEvent();
        event.setJws("jws");
        listener.onAppEvent(event);
        verify(eventService, times(1)).getEvents(anyString());*/
    }
}
