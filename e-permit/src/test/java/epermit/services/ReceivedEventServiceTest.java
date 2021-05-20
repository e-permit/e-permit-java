package epermit.services;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import epermit.events.EventHandler;
import epermit.events.EventValidator;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.ReceivedEventRepository;
import epermit.utils.JwsUtil;

@ExtendWith(MockitoExtension.class)
public class ReceivedEventServiceTest {
    @Mock
    private JwsUtil jwsUtil;
    @Mock
    private ReceivedEventRepository receivedEventRepository;
    @Spy
    private Map<String, EventHandler> eventHandlers;
    @Spy
    private Map<String, EventValidator> eventValidators;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private EPermitProperties properties;

    @InjectMocks
    private ReceivedEventService eventService;

    @Test
    void handleTest() {}

    @Test
    void handleReceivedEventTest() {}
}
