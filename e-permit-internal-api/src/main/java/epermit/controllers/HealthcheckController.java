package epermit.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.models.dtos.HealthCheckResult;
import epermit.services.AuthorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/healthcheck")
@Tag(name = "Health Check", description = "Health Check")
public class HealthcheckController {
    private final AuthorityService authorityService;

    @GetMapping()
    @Operation(summary = "Health check all known authorities", description = "Status of all remote authorities")
    public HealthCheckResult healthcheck() {
        return authorityService.healthcheck();
    }
}
