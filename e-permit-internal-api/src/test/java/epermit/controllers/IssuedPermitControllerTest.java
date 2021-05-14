package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.createpermit.CreatePermitCommand;
import epermit.commands.revokepermit.RevokePermitCommand;
import epermit.common.CommandResult;
import epermit.dtos.IssuedPermitDto;
import epermit.entities.IssuedPermit;
import epermit.repositories.IssuedPermitRepository;

@ExtendWith(MockitoExtension.class)public class IssuedPermitControllerTest {
    @Mock
    IssuedPermitRepository issuedPermitRepository;

    @Mock
    Pipeline pipeline;

    @Test
    void getAllShouldReturnDto() {
        IssuedPermitController controller =
                new IssuedPermitController(issuedPermitRepository, new ModelMapper(), pipeline);
        Pageable pageable = PageRequest.of(2, 23);
        Page<IssuedPermit> permits = mock(Page.class);
        when(issuedPermitRepository.findAll(isA(Pageable.class))).thenReturn(permits);
        ResponseEntity<Page<IssuedPermitDto>> p = controller.getAll(pageable);
    }

    @Test
    void getByIdShouldReturnDto() {
        IssuedPermitController controller =
                new IssuedPermitController(issuedPermitRepository, new ModelMapper(), pipeline);
        IssuedPermit permit = new IssuedPermit();
        when(issuedPermitRepository.findById(Long.valueOf(1))).thenReturn(Optional.of(permit));
        IssuedPermitDto dto = controller.getById(Long.valueOf(1));
        assertNotNull(dto);
    }

    @Test
    void getByPermitIdShouldReturnDto() {
        IssuedPermitController controller =
                new IssuedPermitController(issuedPermitRepository, new ModelMapper(), pipeline);
        IssuedPermit permit = new IssuedPermit();
        when(issuedPermitRepository.findOneByPermitId("TR")).thenReturn(Optional.of(permit));
        IssuedPermitDto dto = controller.getByPermitId("TR");
        assertNotNull(dto);
    }

    @Test
    void createTest() {
        IssuedPermitController controller =
                new IssuedPermitController(issuedPermitRepository, new ModelMapper(), pipeline);
        CreatePermitCommand cmd = new CreatePermitCommand();
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.post(cmd);
        assertTrue(r.isOk());
    }

    @Test
    void revokeTest() {
        IssuedPermitController controller =
                new IssuedPermitController(issuedPermitRepository, new ModelMapper(), pipeline);
        RevokePermitCommand cmd = new RevokePermitCommand();
        cmd.setComment("TR");
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.revoke("TR", cmd);
        assertTrue(r.isOk());
    }
}
