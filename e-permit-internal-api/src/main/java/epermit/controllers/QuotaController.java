package epermit.controllers;

import java.util.Comparator;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.QuotaDto;
import epermit.models.inputs.AddQuotaInput;
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


    @PostMapping("/add")
    public void addQuota(@RequestBody @Valid AddQuotaInput input) {
        log.info("Authority add quota  request. {}", input);
        AuthorityDto authority = service.getByCode(input.getAuthorityCode());
        Optional<QuotaDto> lastQuota = authority.getQuotas().stream()
                .filter(x-> x.getPermitIssuer().equals(input.getAuthorityCode()))
                .sorted(Comparator.comparingInt(QuotaDto::getEndNumber).reversed()).findFirst();
        CreateQuotaInput createQuotaInput = new CreateQuotaInput();
        createQuotaInput.setAuthorityCode(input.getAuthorityCode());
        createQuotaInput.setPermitType(input.getPermitType());
        createQuotaInput.setPermitYear(input.getPermitYear());
        createQuotaInput.setStartNumber(lastQuota.isEmpty() ? 1 : lastQuota.get().getEndNumber() + 1);
        createQuotaInput.setEndNumber(createQuotaInput.getStartNumber() + input.getQuantity() -1);
        service.createQuota(createQuotaInput);
    }
}
