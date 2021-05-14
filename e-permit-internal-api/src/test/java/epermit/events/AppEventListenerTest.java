package epermit.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class AppEventListenerTest {
    @Mock RestTemplate restTemplate;

    @Test
    void test(){
        AppEvent event = new AppEvent();
        event.setUri("uri");
        event.setJws("jws");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(event.getJws(), headers);

        when(restTemplate.postForEntity(event.getUri(), request, Boolean.class)).thenReturn(ResponseEntity.ok(true));
        AppEventListener listener = new AppEventListener(restTemplate);
        listener.onAppEvent(event);
        verify(restTemplate).postForEntity(event.getUri(), request, Boolean.class);
    }
    
}
