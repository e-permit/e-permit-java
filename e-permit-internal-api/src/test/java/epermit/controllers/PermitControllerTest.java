package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.services.PermitService;

@ExtendWith(MockitoExtension.class)
public class PermitControllerTest {
    @Mock
    PermitService permitService;

    @InjectMocks
    PermitController controller;

    @Test
    void getAllTest() {
        List<PermitListItem> permits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PermitListItem dto = new PermitListItem();
            permits.add(dto);
        }
        Page<PermitListItem> pagedList = new PageImpl<>(permits);
        when(permitService.getAll(any())).thenReturn(pagedList);
        Page<PermitListItem> result = controller.getAll(Map.of());
        assertEquals(10, result.getTotalElements());
    }

    @Test
    void getByIdTest() {
        PermitDto permit = new PermitDto();
        UUID id = UUID.randomUUID();
        when(permitService.getById(id)).thenReturn(permit);
        PermitDto dto = controller.getById(id);
        assertNotNull(dto);
        verify(permitService, times(1)).getById(id);
    }


    @Test
    void createTest() {
        CreatePermitInput input = new CreatePermitInput();
        when(permitService.createPermit(input)).thenReturn(CreatePermitResult.success("ABC", "ABC"));
        CreatePermitResult r = controller.createPermit(input);
        assertEquals("ABC", r.getPermitId());
        verify(permitService, times(1)).createPermit(input);
    }

    @Test
    void revokeTest() {
        String id = "TR-UZ-2022-1-1";
        controller.revoke(id);
        verify(permitService, times(1)).revokePermit(eq(id));
    }

    @Test
    void usedTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        PermitUsedInput input = new PermitUsedInput();
        controller.setUsed("TR", input);
        verify(permitService, times(1)).permitUsed("TR", input);
    }
}
