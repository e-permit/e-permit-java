package epermit.controllers;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.PermitListInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.services.PermitService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/permits")
public class PermitController {

    private final PermitService permitService;

    @GetMapping()
    public Page<PermitDto> getAll(@RequestParam Map<String, Object> params) {
        PermitListInput input = GsonUtil.fromMap(params, PermitListInput.class);
        Page<PermitDto> r = permitService.getAll(input);
        return r;
    }


    @GetMapping("/{id}")
    public PermitDto getById(@PathVariable("id") Long id) {
        return permitService.getById(id);
    }

    @PostMapping("/{id}/activities")
    public void setUsed(@PathVariable("id") String id, @RequestBody @Valid PermitUsedInput input) {
        log.info("Permit used request. {}, {}", id, input);
        permitService.usePermit(id, input);
    }
}
