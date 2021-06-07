package epermit.controllers;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import epermit.models.inputs.IssuedPermitListInput;
import epermit.models.results.CreatePermitResult;
import epermit.services.IssuedPermitService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/issued_permits")
public class IssuedPermitController {

    private final IssuedPermitService service;

    @GetMapping()
    public Page<IssuedPermitDto> getAll(@RequestParam Map<String,Object> params) {
        IssuedPermitListInput input = GsonUtil.fromMap(params, IssuedPermitListInput.class);
        Page<IssuedPermitDto> r = service.getAll(input);
        return r;
    }

    @GetMapping("/{id}")
    public IssuedPermitDto getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping()
    public CreatePermitResult createPermit(@RequestBody @Valid CreatePermitInput input) {
        return service.createPermit(input);
    }

    @DeleteMapping("/{id}")
    public void revoke(@PathVariable("id") Long id) {
        service.revokePermit(id);
    }
}
