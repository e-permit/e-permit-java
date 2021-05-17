package epermit.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.AuthorityDto;
import epermit.models.CommandResult;
import epermit.models.CreateAuthorityInput;
import epermit.models.CreateQuotaInput;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authorities")
public class AuthorityController {
    private final AuthorityService service;


    @GetMapping()
    public List<AuthorityDto> getAll() {
        return service.getAll();
    }

    @GetMapping("{code}")
    public AuthorityDto getByCode(String code) {
        return service.getByCode(code);
    }

    @PostMapping()
    public CommandResult create(@RequestBody @Valid CreateAuthorityInput input) {
        return service.create(input);
    }

    @PostMapping("/createquota")
    public CommandResult createQuota(@RequestBody @Valid CreateQuotaInput input) {
        return service.createQuota(input);
    }

    @PatchMapping("/{id}/enablequota")
    public CommandResult enableQuota(@RequestParam Long id) {
        return service.enableQuota(id);
    }

}
