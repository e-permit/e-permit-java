package epermit.controllers;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import epermit.commons.GsonUtil;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.dtos.PermitListParams;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.services.PermitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/permits")
@CrossOrigin(origins = "*")
public class PermitController {
    private final PermitService permitService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') OR hasRole('VERIFIER')")
    public Page<PermitListItem> getAll(@RequestParam Map<String, Object> params) {
        PermitListParams input = GsonUtil.fromMap(params, PermitListParams.class);
        Page<PermitListItem> r = permitService.getAll(input);
        return r;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('VERIFIER')")
    public PermitDto getById(@PathVariable("id") UUID id) {
        return permitService.getById(id);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('VERIFIER')")
    public Optional<PermitDto> getByPermitId(@PathVariable("id") String id) {
        return permitService.getByPermitId(id);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public CreatePermitResult createPermit(@RequestBody @Valid CreatePermitInput input) {
        log.info("Permit create request. {}", input);
        return permitService.createPermit(input);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void revoke(@PathVariable("id") String id) {
        log.info("Revoke permit request. {}", id);
        permitService.revokePermit(id);
    }

    @PostMapping("/{id}/activities")
    @PreAuthorize("hasRole('ADMIN') OR hasRole('VERIFIER')")
    public void setUsed(@PathVariable("id") String id, @RequestBody @Valid PermitUsedInput input) {
        log.info("Permit used request. {}, {}", id, input);
        permitService.permitUsed(id, input);
    }
}
