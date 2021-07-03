package epermit.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.PermitUsedInput;
import epermit.services.PermitService;

@ExtendWith(MockitoExtension.class)
public class PermitControllerTest {
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
    void usedTest() {
        PermitUsedInput input = new PermitUsedInput();
        controller.setUsed("TR", input);
        verify(permitService, times(1)).permitUsed("TR", input);
    }
}
