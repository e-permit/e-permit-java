package epermit.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.inputs.CreateQuotaInput;
import epermit.services.AuthorityService;

@ExtendWith(MockitoExtension.class)

public class AuthorityQuotaControllerTest {
    @Mock
    AuthorityService authorityService;

    @InjectMocks
    QuotaController controller;

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        controller.createQuota(input);
        verify(authorityService, times(1)).createQuota(input);
    }
}


