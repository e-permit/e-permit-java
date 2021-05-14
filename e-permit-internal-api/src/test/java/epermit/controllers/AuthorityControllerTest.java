package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.createauthority.CreateAuthorityCommand;
import epermit.commands.createquota.CreateQuotaCommand;
import epermit.commands.enablequota.EnableQuotaCommand;
import epermit.common.CommandResult;
import epermit.dtos.AuthorityDto;
import epermit.entities.Authority;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)

public class AuthorityControllerTest {
    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    Pipeline pipeline;

    @Test
    void getAllShouldReturnDto() {
        AuthorityController controller =
                new AuthorityController(pipeline, authorityRepository, new ModelMapper());
        Authority authority = new Authority();
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        List<AuthorityDto> dtos = controller.getAll();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeShouldReturnDto() {
        AuthorityController controller =
                new AuthorityController(pipeline, authorityRepository, new ModelMapper());
        Authority authority = new Authority();
        when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.of(authority));
        AuthorityDto dto = controller.getByCode("TR");
        assertNotNull(dto);
    }
    
    @Test
    void createTest() {
        AuthorityController controller =
                new AuthorityController(pipeline, authorityRepository, new ModelMapper());
        CreateAuthorityCommand cmd = new CreateAuthorityCommand();
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success(new Authority()));
        CommandResult r = controller.create(cmd);
        assertTrue(r.isOk());
    }

    @Test
    void createQuotaTest() {
        AuthorityController controller =
                new AuthorityController(pipeline, authorityRepository, new ModelMapper());
        CreateQuotaCommand cmd = new CreateQuotaCommand();
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.createQuota(cmd);
        assertTrue(r.isOk());
    }

    @Test
    void enableQuotaTest() {
        AuthorityController controller =
                new AuthorityController(pipeline, authorityRepository, new ModelMapper());
        EnableQuotaCommand cmd = new EnableQuotaCommand();
        cmd.setId(1);
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.enableQuota(cmd);
        assertTrue(r.isOk());
    }
}
