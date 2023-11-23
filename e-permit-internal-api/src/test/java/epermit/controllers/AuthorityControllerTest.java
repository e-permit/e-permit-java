package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.services.AuthorityService;

@ExtendWith(MockitoExtension.class)

public class AuthorityControllerTest {
    @Mock
    AuthorityService authorityService;

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
        input.setClientId("apiUri");
        input.setCode("UZ");
        input.setName("name");

        controller.create(input);
        verify(authorityService, times(1)).create(eq(input));
    }
}
