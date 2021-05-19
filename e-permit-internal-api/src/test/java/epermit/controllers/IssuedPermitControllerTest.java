package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.services.IssuedPermitService;

@ExtendWith(MockitoExtension.class)
public class IssuedPermitControllerTest {
    @Mock
    IssuedPermitService issuedPermitService;

    @InjectMocks
    IssuedPermitController controller;

    @Test
    void getAllTest() {
        Pageable pageable = PageRequest.of(2, 20);
        List<IssuedPermitDto> permits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            IssuedPermitDto dto = new IssuedPermitDto();
            permits.add(dto);
        }
        Page<IssuedPermitDto> pagedList = new PageImpl<>(permits);
        
        when(issuedPermitService.getAll(eq("UA"), isA(Pageable.class))).thenReturn(pagedList);
        Page<IssuedPermitDto> result = controller.getAll("UA", pageable);
        assertEquals(10, result.getTotalElements());
        verify(issuedPermitService, times(1)).getAll("UA", pageable);
    }

    @Test
    void getByIdTest() {
        IssuedPermitDto permit = new IssuedPermitDto();
        when(issuedPermitService.getById(Long.valueOf(1))).thenReturn(permit);
        IssuedPermitDto dto = controller.getById(Long.valueOf(1));
        assertNotNull(dto);
        verify(issuedPermitService, times(1)).getById(Long.valueOf(1));
    }


    @Test
    void createTest() {
        CreatePermitInput input = new CreatePermitInput();
        when(issuedPermitService.createPermit(input)).thenReturn("ABC");
        Map<String, String> r = controller.createPermit(input);
        assertEquals("ABC", r.get("permitId"));
        verify(issuedPermitService, times(1)).createPermit(input);
    }

    @Test
    void revokeTest() {
        controller.revoke(Long.valueOf(1));
        verify(issuedPermitService, times(1)).revokePermit(eq(Long.valueOf(1)), anyString());
    }
}
