package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.services.AuthorityService;

@ExtendWith(MockitoExtension.class)

public class AuthorityControllerTest {
    @Mock
    AuthorityService authorityService;

    @Mock 
    RestTemplate restTemplate;

    @InjectMocks
    AuthorityController controller;

    @Test
    void getAllTest() {
        AuthorityDto authority = new AuthorityDto();
        when(authorityService.getAll()).thenReturn(List.of(authority));
        List<AuthorityDto> dtos = controller.getAll();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeTest() {
        AuthorityDto authority = new AuthorityDto();
        authority.setCode("UA");
        when(authorityService.getByCode("TR")).thenReturn(authority);
        AuthorityDto dto = controller.getByCode("TR");
        assertNotNull(dto);
        assertEquals("UA", dto.getCode());
    }
    
    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        controller.create(input);
        verify(authorityService, times(1)).create(eq(input), any());
    }

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        controller.createQuota(input);
        verify(authorityService, times(1)).createQuota(input);
    }

    @Test
    void enableQuotaTest() {
        controller.enableQuota(1);
        verify(authorityService, times(1)).enableQuota(1);
    }
}
