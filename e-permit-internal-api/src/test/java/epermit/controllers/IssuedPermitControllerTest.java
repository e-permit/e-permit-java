package epermit.controllers;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.results.CommandResult;
import epermit.services.IssuedPermitService;

@ExtendWith(MockitoExtension.class)public class IssuedPermitControllerTest {
    @Mock
    IssuedPermitService issuedPermitService;

    @InjectMocks
    IssuedPermitController controller;

    @Test
    void getAllShouldReturnDto() {
        Pageable pageable = PageRequest.of(2, 23);
        Page<IssuedPermitDto> permits ;
        when(issuedPermitService.getAll(isA(Pageable.class))).thenReturn(null);
        ResponseEntity<Page<IssuedPermitDto>> p = controller.getAll(pageable);
    }

    @Test
    void getByIdShouldReturnDto() {
        IssuedPermitDto permit = new IssuedPermitDto();
        when(issuedPermitService.getById(Long.valueOf(1))).thenReturn(permit);
        IssuedPermitDto dto = controller.getById(Long.valueOf(1));
        assertNotNull(dto);
    }


    @Test
    void createTest() {
        CreatePermitInput input = new CreatePermitInput();
        CommandResult r = controller.createPermit(input);
        assertTrue(r.isOk());
    }

    @Test
    void revokeTest() {
        CommandResult r =  controller.revoke(Long.valueOf(1));
        assertTrue(r.isOk());
    }
}
