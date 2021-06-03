package epermit.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
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
        Pageable pageable = PageRequest.of(2, 20);
        List<PermitDto> permits = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PermitDto dto = new PermitDto();
            permits.add(dto);
        }
        Page<PermitDto> pagedList = new PageImpl<>(permits);
        
        when(permitService.getAll(isA(Pageable.class))).thenReturn(pagedList);
        Page<PermitDto> result = controller.getAll(pageable);
        assertEquals(10, result.getTotalElements());
        verify(permitService, times(1)).getAll(pageable);
    }

    @Test
    void usedTest() {
        PermitUsedInput input = new PermitUsedInput();
        controller.setUsed("TR", input);
        verify(permitService, times(1)).usePermit("TR", input);
    }
}
