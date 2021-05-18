package epermit.controllers;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CommandResult;
import epermit.services.PermitService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permits")
public class PermitController {

    private final PermitService permitService;

    @GetMapping()
    public ResponseEntity<Page<PermitDto>> getAll(Pageable pageable) {
        return new ResponseEntity<>(permitService.getAll(pageable), HttpStatus.OK);
    }

    @PatchMapping("{id}/used")
    public ResponseEntity<CommandResult> setUsed(@RequestBody @Valid PermitUsedInput input ) {
        CommandResult r = permitService.usePermit(input);
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }
}
