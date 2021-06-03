package epermit.controllers;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.PermitUsedInput;
import epermit.services.PermitService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permits")
public class PermitController {

    private final PermitService permitService;

    @GetMapping()
    public Page<PermitDto> getAll(Pageable pageable) {
        return permitService.getAll(pageable);
    }

    @PostMapping("/{id}/usages")
    public void setUsed(@PathVariable("id") String id, @RequestBody @Valid PermitUsedInput input) {
        permitService.usePermit(id, input);
    }
}
