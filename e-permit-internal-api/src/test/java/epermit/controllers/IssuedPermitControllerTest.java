package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.results.CreatePermitResult;
import epermit.services.PermitService;

@ExtendWith(MockitoExtension.class)
public class IssuedPermitControllerTest {
    @Mock
    PermitService permitService;

    @InjectMocks
    PermitController controller;

    @Test
    void getAllTest() {
        List<PermitDto> permits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PermitDto dto = new PermitDto();
            permits.add(dto);
        }
        Page<PermitDto> pagedList = new PageImpl<>(permits);
        when(permitService.getAll(any())).thenReturn(pagedList);
        Page<PermitDto> result = controller.getAll(Map.of());
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
}
