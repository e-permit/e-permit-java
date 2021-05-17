package epermit.controllers;

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
        controller.create();
        //assertTrue(r.isOk());
    }

    @Test
    void enableTest() {
        controller.enable();
        //assertTrue(r.isOk());
    }
}
