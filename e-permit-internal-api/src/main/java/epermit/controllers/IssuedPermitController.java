package epermit.controllers;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.results.CommandResult;
import epermit.services.IssuedPermitService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issued_permits")
public class IssuedPermitController {

    private final IssuedPermitService service;

    @GetMapping()
    public Page<IssuedPermitDto> getAll(@RequestParam(required = false) String issuedFor,
            Pageable pageable) {
        return service.getAll(issuedFor, pageable);
    }

    @GetMapping("{id}")
    public IssuedPermitDto getById(Long id) {
        return service.getById(id);
    }

    @PostMapping()
    public Map<String, String> createPermit(@RequestBody @Valid CreatePermitInput input) {
        String permitId = service.createPermit(input);
        return Map.of("permitId", permitId);
    }

    @PatchMapping("{id}/revoke")
    public void revoke(@PathVariable Long id) {
        service.revokePermit(id, "comment");
    }
}
