package epermit.events;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)
public class AppEventListenerTest {
    @Mock EventService eventService;

    @Test
    void shouldNotCallGetEvents() {
        when(eventService.handle("jws")).thenReturn(EventHandleResult.success());
        AppEventListener listener = new AppEventListener(eventService);
        AppEvent event = new AppEvent();
        event.setJws("jws");
        listener.onAppEvent(event);
        verify(eventService, times(0)).getEvents(anyString());
    }

    @Test
    void shouldCallGetEventsIfNotExist() {
        when(eventService.handle("jws")).thenReturn(EventHandleResult.fail("NOTEXIST_PREVIOUSEVENT"));
        AppEventListener listener = new AppEventListener(eventService);
        AppEvent event = new AppEvent();
        event.setJws("jws");
        listener.onAppEvent(event);
        verify(eventService, times(1)).getEvents(anyString());
    }
}
