package epermit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import epermit.appevents.LedgerEventCreated;
import epermit.ledgerevents.LedgerEventUtil;

@ExtendWith(MockitoExtension.class)
public class LedgerEventCreatedListenerTest {
    @Mock RestTemplate restTemplate;
    @Mock LedgerEventUtil ledgerEventUtil;

    @InjectMocks
    AppEventListener listener;

    @Test
    void test(){
        LedgerEventCreated event = new LedgerEventCreated();
        event.setUri("uri");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //HttpEntity<String> request = new HttpEntity<String>(event.getJws(), headers);
        //when(jwsUtil.getJwsHeader(anyString())).thenReturn(headers);
        //when(restTemplate.postForEntity(event.getUri(), request, Boolean.class)).thenReturn(ResponseEntity.ok(true));
        //listener.onAppEvent(event);
        //verify(restTemplate).postForEntity(event.getUri(), request, Boolean.class);
    }
    
}
