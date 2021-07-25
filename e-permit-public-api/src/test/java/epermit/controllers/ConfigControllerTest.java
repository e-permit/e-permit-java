package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.dtos.AuthorityConfig;
import epermit.services.ConfigService;

@ExtendWith(MockitoExtension.class)
public class ConfigControllerTest {
    @Mock
    ConfigService configService;

    @InjectMocks
    ConfigController controller;

    @Test
    void getConfigTest(){
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("TR");
        when(configService.getConfig()).thenReturn(config);
        AuthorityConfig result = controller.getConfig();
        assertEquals(config, result);
    }
}
