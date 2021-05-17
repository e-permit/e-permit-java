package epermit.controllers;

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
import org.springframework.web.bind.annotation.RestController;
import epermit.models.CommandResult;
import epermit.models.CreatePermitInput;
import epermit.models.IssuedPermitDto;
import epermit.services.IssuedPermitService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issued_permits")
public class IssuedPermitController {

    private final IssuedPermitService service;

    @GetMapping()
    public ResponseEntity<Page<IssuedPermitDto>> getAll(Pageable pageable) {
        return new ResponseEntity<>(service.getAll(pageable), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public IssuedPermitDto getById(Long id) {
        return service.getById(id);
    }

    @PostMapping()
    public CommandResult createPermit(@RequestBody @Valid CreatePermitInput input) {
        return service.createPermit(input);
    }

    @PatchMapping("{id}/revoke")
    public CommandResult revoke(@PathVariable Long id) {
        return service.revokePermit(id, "comment");
    }
}
