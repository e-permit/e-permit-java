package epermit.controllers;

import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.inputs.CreateQuotaInput;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/authority_quotas")
@PreAuthorize("hasRole('ADMIN')")
public class QuotaController {
    private final AuthorityService service;

    @PostMapping()
    public void createQuota(@RequestBody @Valid CreateQuotaInput input) {
        log.info("Authority quota create request. {}", input);
        service.createQuota(input);
    }
}
