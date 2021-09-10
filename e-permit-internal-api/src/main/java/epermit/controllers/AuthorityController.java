package epermit.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import epermit.commons.GsonUtil;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/authorities")
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

    @PostMapping()
    public void create(@RequestBody @Valid CreateAuthorityInput input) {
        log.info("Authority create request. {}", input);
        String r = restTemplate.getForObject(input.getApiUri() + "/epermit-configuration",
                String.class);
        AuthorityConfig config = GsonUtil.getGson().fromJson(r, AuthorityConfig.class);
        log.info(GsonUtil.getGson().toJson(config));
        service.create(input, config);
    }
}
