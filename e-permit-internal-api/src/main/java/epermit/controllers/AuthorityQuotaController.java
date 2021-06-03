package epermit.controllers;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.inputs.CreateQuotaInput;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authority_quotas")
public class AuthorityQuotaController {
    private final AuthorityService service;

    @PostMapping()
    public void createQuota(@RequestBody @Valid CreateQuotaInput input) {
        service.createQuota(input);
    }

    @PatchMapping("/{id}/enable")
    public void enableQuota(@PathVariable("id") Integer id) {
        service.enableQuota(id);
    }
}
