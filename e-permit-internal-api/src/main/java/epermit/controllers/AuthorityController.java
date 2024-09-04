package epermit.controllers;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import epermit.models.dtos.AuthorityListItem;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.services.AuthorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/authorities")
@Tag(name = "Authorities", description = "Authority Management APIs")
public class AuthorityController {
    private final AuthorityService service;
    private final RestTemplate restTemplate;

    @GetMapping()
    @Operation(summary = "Get all authorities", description = "Returns all known authorities")
    public List<AuthorityListItem> getAll() {
        return service.getAll();
    }

    @GetMapping("/{code}")
    @Operation(summary = "Get authority by code", description = "Get a Authority object by specifying its code")
    public AuthorityDto getByCode(
            @Parameter(description = "Authority code", example = "A") @PathVariable("code") String code) {
        return service.getByCode(code);
    }

    @SneakyThrows
    @PostMapping()
    @Operation(summary = "Create authority", description = "Create new authority")
    public void create(@RequestBody @Valid CreateAuthorityInput input) {
        log.info("Authority create request. {}", input);
        HttpHeaders headers = new HttpHeaders();
        /*if (properties.getXroadUrl().isPresent()
                && input.getPublicApiUri().startsWith(properties.getXroadUrl().get())) {
            headers.add("X-Road-Client", properties.getXroadClientId().get());
        }*/
        ResponseEntity<AuthorityConfig> result = restTemplate
                .getForEntity(input.getPublicApiUri(), AuthorityConfig.class, headers);
        if (result.getStatusCode() == HttpStatus.OK) {
            log.info("Authority config got successfully. Result: {}", result.getBody());
            service.create(input, result.getBody());
        } else {
            throw new EpermitValidationException("Couldn't get authority config",
                    ErrorCodes.REMOTE_ERROR);
        }
    }

    @PostMapping("/{code}/quotas")
    @Operation(summary = "Create quota", description = "Create quota for given authority")
    public void createQuota(
            @Parameter(description = "Authority code", example = "A") @PathVariable("code") String code,
            @RequestBody @Valid CreateQuotaInput input) {
        log.info("Authority quota create request. {}", input);
        service.createQuota(code, input);
    }
}
