package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.common.PermitProperties;
import epermit.dtos.ConfigDto;
import epermit.entities.Key;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;

@ExtendWith(MockitoExtension.class)
public class ConfigControllerTest {
    @Mock
    PermitProperties props;
    @Mock
    KeyRepository keyRepository;
    @Mock
    AuthorityRepository authorityRepository;

    @Test
    void getTest() {
        when(props.getKeyPassword()).thenReturn("123456");
        KeyService keyService = new KeyService(props, keyRepository, authorityRepository);
        Key key = keyService.create("1");
        List<Key> keys = new ArrayList<>();
        keys.add(key);
        when(props.getIssuerCode()).thenReturn("TR");
        when(props.getIssuerVerifyUri()).thenReturn("http://localhost");
        when(keyRepository.findAll()).thenReturn(keys);
        ConfigController controller = new ConfigController(props, keyRepository, authorityRepository);
        ConfigDto dto = controller.getConfig();
        assertEquals("http://localhost", dto.getVerifyUri());
        assertEquals("TR", dto.getCode());
    }
}
