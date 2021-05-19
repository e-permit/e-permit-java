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
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.services.AuthorityService;
import epermit.services.CreatedEventService;
import epermit.utils.KeyUtil;

@ExtendWith(MockitoExtension.class)
public class ConfigControllerTest {
    @Mock
    AuthorityService authorityService;

    @InjectMocks
    ConfigController controller;

    @Test
    void getConfigTest(){
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("TR");
        config.setName("Turkey");
        config.setVerifyUri("https://localhost:3001");
        when(authorityService.getConfig()).thenReturn(config);
        AuthorityConfig result = controller.getConfig();
        assertEquals(config, result);
    }
}
