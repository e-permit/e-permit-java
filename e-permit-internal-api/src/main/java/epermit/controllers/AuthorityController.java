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
    public void create(@RequestBody @Valid CreateAuthorityInput input) {
        service.create(input);
    }

    @PostMapping("/createquota")
    public void createQuota(@RequestBody @Valid CreateQuotaInput input) {
        service.createQuota(input);
    }

    @PatchMapping("/{id}/enablequota")
    public void enableQuota(@RequestParam Long id) {
        service.enableQuota(id);
    }

}
