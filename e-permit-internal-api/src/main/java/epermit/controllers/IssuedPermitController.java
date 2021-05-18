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
    public ResponseEntity<Page<IssuedPermitDto>> getAll(Pageable pageable) {
        return new ResponseEntity<>(service.getAll(pageable), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public IssuedPermitDto getById(Long id) {
        return service.getById(id);
    }

    @PostMapping()
    public ResponseEntity<CommandResult> createPermit(@RequestBody @Valid CreatePermitInput input) {
        CommandResult r = service.createPermit(input);
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("{id}/revoke")
    public ResponseEntity<CommandResult> revoke(@PathVariable Long id) {
        CommandResult r =  service.revokePermit(id, "comment");
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }
}
