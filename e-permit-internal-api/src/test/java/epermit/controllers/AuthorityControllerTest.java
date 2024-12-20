package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.AuthorityListItem;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.services.AuthorityService;

@ExtendWith(MockitoExtension.class)
public class AuthorityControllerTest {
    @Mock
    RestTemplate restTemplate;

    @Mock
    AuthorityService authorityService;

    @InjectMocks
    AuthorityController controller;

    @Test
    void getAllTest() {
        AuthorityListItem authority = new AuthorityListItem();
        when(authorityService.getAll()).thenReturn(List.of(authority));
        List<AuthorityListItem> dtos = controller.getAll();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeTest() {
        AuthorityDto authority = new AuthorityDto();
        authority.setCode("UA");
        when(authorityService.getByCode("A")).thenReturn(authority);
        AuthorityDto dto = controller.getByCode("A");
        assertNotNull(dto);
        assertEquals("UA", dto.getCode());
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setPublicApiUri("apiUri");
        input.setCode("B");
        input.setName("name");
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("B");
        config.setName("name");
        when(restTemplate.getForEntity("apiUri", AuthorityConfig.class, new HttpHeaders()))
                .thenReturn(new ResponseEntity<>(config, HttpStatus.OK));
        controller.create(input);
        verify(authorityService, times(1)).create(eq(input), eq(config));
    }

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        controller.createQuota("A", input);
        verify(authorityService, times(1)).createQuota("A", input);
    }
}
