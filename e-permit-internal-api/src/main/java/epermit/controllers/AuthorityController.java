package epermit.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.models.results.CommandResult;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authorities")
public class AuthorityController {
    private final AuthorityService service;
    private final RestTemplate restTemplate;


    @GetMapping()
    public List<AuthorityDto> getAll() {
        return service.getAll();
    }

    @GetMapping("{code}")
    public AuthorityDto getByCode(String code) {
        return service.getByCode(code);
    }

    @PostMapping()
    public void create(@RequestBody @Valid CreateAuthorityInput input) {
        AuthorityConfig config =
                restTemplate.getForObject(input.getApiUri(), AuthorityConfig.class);
        service.create(input, config);
    }

    @PostMapping("/createquota")
    public void createQuota(@RequestBody @Valid CreateQuotaInput input) {
        service.createQuota(input);
    }

    @PatchMapping("/{id}/enablequota")
    public  void enableQuota(@RequestParam Integer id) {
        service.enableQuota(id);
    }

}
