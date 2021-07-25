package epermit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import epermit.ledgerevents.LedgerEventCreated;
import epermit.utils.JwsUtil;

@ExtendWith(MockitoExtension.class)
public class LedgerEventCreatedListenerTest {
    @Mock RestTemplate restTemplate;
    @Mock JwsUtil jwsUtil;

    @InjectMocks
    AppEventListener listener;

    @Test
    void test(){
        LedgerEventCreated event = new LedgerEventCreated();
        event.setUri("uri");
        event.setProof("jws");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //HttpEntity<String> request = new HttpEntity<String>(event.getJws(), headers);
        //when(jwsUtil.getJwsHeader(anyString())).thenReturn(headers);
        //when(restTemplate.postForEntity(event.getUri(), request, Boolean.class)).thenReturn(ResponseEntity.ok(true));
        listener.onAppEvent(event);
        //verify(restTemplate).postForEntity(event.getUri(), request, Boolean.class);
    }
    
}
