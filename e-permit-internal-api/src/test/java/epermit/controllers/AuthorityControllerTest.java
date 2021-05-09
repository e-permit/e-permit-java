package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import an.awesome.pipelinr.Pipeline;
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
    void getByCodeShouldReturnDto() {
        AuthorityController controller =
                new AuthorityController(pipeline, authorityRepository, new ModelMapper());
        Authority authority = new Authority();
        when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.of(authority));
        AuthorityDto dto = controller.getByCode("TR");
        assertNotNull(dto);
    }
}
