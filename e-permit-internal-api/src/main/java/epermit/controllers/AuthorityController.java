package epermit.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
public class AuthorityController {
    private final AuthorityService service;

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
        service.create(input);
    }
}
