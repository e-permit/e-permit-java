package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Key;
import epermit.models.AuthorityConfig;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;

@ExtendWith(MockitoExtension.class)
public class ConfigControllerTest {
    @Mock
    EPermitProperties props;

    @Mock
    KeyRepository keyRepository;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    KeyService keyService;

    @InjectMocks
    ConfigController controller;

    @Test
    void getTest() {
        when(props.getKeyPassword()).thenReturn("123456");
        Key key = keyService.create("1");
        List<Key> keys = new ArrayList<>();
        keys.add(key);
        when(props.getIssuerCode()).thenReturn("TR");
        when(props.getIssuerVerifyUri()).thenReturn("http://localhost");
        when(keyRepository.findAll()).thenReturn(keys);
        AuthorityConfig dto = controller.getConfig();
        assertEquals("http://localhost", dto.getVerifyUri());
        assertEquals("TR", dto.getCode());
    }
}
