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
    public ResponseEntity<CommandResult> create(@RequestBody @Valid CreateAuthorityInput input) {
        AuthorityConfig config =
                restTemplate.getForObject(input.getApiUri(), AuthorityConfig.class);
        CommandResult r = service.create(input, config);
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/createquota")
    public ResponseEntity<CommandResult> createQuota(@RequestBody @Valid CreateQuotaInput input) {
        CommandResult r = service.createQuota(input);
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/enablequota")
    public ResponseEntity<CommandResult> enableQuota(@RequestParam Integer id) {
        CommandResult r = service.enableQuota(id);
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }

}
