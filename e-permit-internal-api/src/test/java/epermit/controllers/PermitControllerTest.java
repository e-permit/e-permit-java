package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import epermit.entities.Permit;
import epermit.models.PermitDto;
import epermit.repositories.PermitRepository;
import epermit.services.PermitService;

@ExtendWith(MockitoExtension.class)
public class PermitControllerTest {
    @Mock
    PermitService permitService;

    @InjectMocks
    PermitController controller;

    @Test
    void getAllTest() {
       Pageable pageable = PageRequest.of(2, 23);
        Page<Permit> permits ;
        when(permitService.getAll(isA(Pageable.class))).thenReturn(null);
        ResponseEntity<Page<PermitDto>> p = controller.getAll(pageable);
    }

    @Test
    void usedTest() {
        controller.setUsed(Long.valueOf(1));
        //assertTrue(r.isOk());
    }
}
