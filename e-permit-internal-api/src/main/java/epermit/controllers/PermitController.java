package epermit.controllers;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.el.MethodNotFoundException;
import javax.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/permits")
@CrossOrigin(origins = "*")
public class PermitController {
    private final PermitService permitService;
    static final String permitAll = "hasAnyRole('ADMIN', 'MANAGER', 'VERIFIER')";
    static final String permitAdmin = "hasAnyRole('ADMIN', 'MANAGER')";

    @GetMapping()
    @PreAuthorize(permitAdmin)
    public Page<PermitListItem> getAll(@RequestParam Map<String, Object> params) {
        PermitListParams input = GsonUtil.fromMap(params, PermitListParams.class);
        Page<PermitListItem> r = permitService.getAll(input);
        return r;
    }

    @GetMapping("/{id}")
    @PreAuthorize(permitAll)
    public PermitDto getById(@PathVariable("id") UUID id) {
        return permitService.getById(id);
    }

    @RequestMapping(value = "/{id}/pdf", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize(permitAll)
    public ResponseEntity<InputStreamResource> getPdfById(@PathVariable("id") String id) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(permitService.generatePdf(id)));
    }


    @GetMapping("/find/{id}")
    @PreAuthorize(permitAll)
    public Optional<PermitDto> getByPermitId(@PathVariable("id") String id) {
        return permitService.getByPermitId(id);
    }

    @PostMapping()
    @PreAuthorize(permitAdmin)
    public CreatePermitResult createPermit(@RequestBody @Valid CreatePermitInput input) {
        log.info("Permit create request. {}", input);
        return permitService.createPermit(input);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(permitAdmin)
    public void revoke(@PathVariable("id") String id) {
        log.info("Revoke permit request. {}", id);
        permitService.revokePermit(id);
    }

    @PostMapping("/{id}/activities")
    @PreAuthorize(permitAll)
    public void setUsed(@PathVariable("id") String id, @RequestBody @Valid PermitUsedInput input) {
        log.info("Permit used request. {}, {}", id, input);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VERIFIER"))) {
            Optional<? extends GrantedAuthority> terminal = auth.getAuthorities().stream()
                    .filter(a -> a.getAuthority().startsWith("TERMINAL_")).findFirst();
            input.setActivityDetails(terminal.get().getAuthority().substring(9));
        }
        permitService.permitUsed(id, input);
    }
}
