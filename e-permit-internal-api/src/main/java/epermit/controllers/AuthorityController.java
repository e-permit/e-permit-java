package epermit.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/authorities")
@PreAuthorize("hasRole('ADMIN')")
public class AuthorityController {
    private final AuthorityService service;
    private final RestTemplate restTemplate;

    @GetMapping()
    public List<AuthorityDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{code}")
    public AuthorityDto getByCode(@PathVariable("code") String code) {
        return service.getByCode(code);
    }

    @SneakyThrows
    @PostMapping()
    public void create(@RequestBody @Valid CreateAuthorityInput input) {
        log.info("Authority create request. {}", input);
        ResponseEntity<AuthorityConfig> result = restTemplate
                .getForEntity(input.getApiUri() + "/epermit-configuration", AuthorityConfig.class);
        if (result.getStatusCode() == HttpStatus.OK) {
            log.info("Authority config got successfully. Result: {}", result.getBody());
            service.create(input, result.getBody());
        } else {
            throw new EpermitValidationException("Couldn't get authority config",
                    ErrorCodes.REMOTE_ERROR);
        }
    }
}
