package epermit.controllers;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.services.KeyService;

@ExtendWith(MockitoExtension.class)
public class KeyControllerTest {
    @Mock
    KeyService keyService;

    @InjectMocks
    KeyController controller;

    @Test
    void createTest() {
        Map<String, String> input = new HashMap<>();
        input.put("key_id", "value");
        controller.create(input);
        verify(keyService, times(1)).create("value");
    }

    @Test
    void enableTest() {
        controller.enable(1);
        verify(keyService, times(1)).enable(1);
    }
}
