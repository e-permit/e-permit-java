package epermit.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;

@ExtendWith(MockitoExtension.class)
public class KeyServiceTest {
    @Mock
    private KeyRepository keyRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private KeyUtil keyUtil;
    @Mock
    private KeyCreatedEventFactory factory;
    @InjectMocks
    private KeyService keyService;

    @Test
    void seedTest() {}
    @Test
    void createTest() {}
    @Test
    void enableTest() {}
}
