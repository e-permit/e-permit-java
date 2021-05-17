package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.AuthorityDto;
import epermit.models.CommandResult;
import epermit.models.CreateAuthorityInput;
import epermit.models.CreateQuotaInput;
import epermit.services.AuthorityService;

@ExtendWith(MockitoExtension.class)

public class AuthorityControllerTest {
    @Mock
    AuthorityService authorityService;

    @InjectMocks
    AuthorityController controller;

    @Test
    void getAllShouldReturnDto() {
        AuthorityDto authority = new AuthorityDto();
        when(authorityService.getAll()).thenReturn(List.of(authority));
        List<AuthorityDto> dtos = controller.getAll();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeShouldReturnDto() {
        AuthorityDto authority = new AuthorityDto();
        when(authorityService.getByCode("TR")).thenReturn(authority);
        AuthorityDto dto = controller.getByCode("TR");
        assertNotNull(dto);
    }
    
    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        CommandResult r =  controller.create(input);
        assertTrue(r.isOk());
    }

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        CommandResult r =  controller.createQuota(input);
        assertTrue(r.isOk());
    }

    @Test
    void enableQuotaTest() {
        CommandResult r =  controller.enableQuota(Long.valueOf(1));
        assertTrue(r.isOk());
    }
}
