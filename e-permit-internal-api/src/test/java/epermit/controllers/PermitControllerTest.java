package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.permitused.PermitUsedCommand;
import epermit.common.CommandResult;
import epermit.common.PermitActivityType;
import epermit.dtos.PermitDto;
import epermit.entities.Permit;
import epermit.repositories.PermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitControllerTest {
    @Mock
    PermitRepository permitRepository;

    @Mock
    Pipeline pipeline;

    @Test
    void getAllTest() {
        PermitController controller =
                new PermitController(permitRepository, pipeline, new ModelMapper());
        Pageable pageable = PageRequest.of(2, 23);
        Page<Permit> permits = mock(Page.class);
        when(permitRepository.findAll(isA(Pageable.class))).thenReturn(permits);
        ResponseEntity<Page<PermitDto>> p = controller.getAll(pageable);
    }

    @Test
    void usedTest() {
        PermitController controller =
                new PermitController(permitRepository, pipeline, new ModelMapper());
        PermitUsedCommand cmd = new PermitUsedCommand();
        cmd.setActivityType(PermitActivityType.ENTERANCE);
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.setUsed("TR", cmd);
        assertTrue(r.isOk());
    }
}
