package epermit.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.CommandResult;
import epermit.models.PermitActivityType;
import epermit.models.PermitDto;
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
    public CommandResult setUsed(@PathVariable Long id) {
        return permitService.usePermit(id, PermitActivityType.ENTERANCE);
    }
}
