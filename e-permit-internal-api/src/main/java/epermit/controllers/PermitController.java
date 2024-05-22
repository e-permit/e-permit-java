package epermit.controllers;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.Valid;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.dtos.PermitListPageParams;
import epermit.models.dtos.PermitListParams;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.services.PermitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/permits")
@CrossOrigin(origins = "*")
@Tag(name = "Permits", description = "Permit Management APIs")
public class PermitController {
    private final PermitService permitService;

    @GetMapping()
    @Operation(summary = "Get permits by page", description = "Get permits by page - filtering, paging sorting by created_at")
    public Page<PermitListItem> getPage(@ParameterObject PermitListPageParams input) {
        Page<PermitListItem> r = permitService.getPage(input);
        return r;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all permits", description = "Get all permits - filtering")
    public List<PermitListItem> getAll(@ParameterObject PermitListParams input) {
        List<PermitListItem> r = permitService.getAll(input);
        return r;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permit", description = "Get permit by specified database id")
    public PermitDto getById(
            @Parameter(description = "Permit database id") @PathVariable("id") UUID id) {
        return permitService.getById(id);
    }

    @GetMapping("/find/{id}")
    @Operation(summary = "Find permit", description = "Find permit by specified permit id")
    public PermitDto getByPermitId(
            @Parameter(description = "Permit Identifier", example = "TR-UZ-2024-1-1") @PathVariable("id") String id) {
        return permitService.getByPermitId(id);
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Get permit pdf", description = "Get permit pdf(base64) by specified permit id")
    public String getBase64PdfById(
            @Parameter(description = "Permit Identifier", example = "TR-UZ-2024-1-1") @PathVariable("id") String id) {
        return Base64.getEncoder().encodeToString(permitService.generatePdf(id));
    }

    @PostMapping()
    @Operation(summary = "Create permit", description = "Create new permit with inputs")
    public CreatePermitResult createPermit(@RequestBody @Valid CreatePermitInput input) {
        log.info("Permit create request. {}", input);
        return permitService.createPermit(input);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke permit", description = "Revoke permit by permit id")
    public void revoke(
            @Parameter(description = "Permit Identifier", example = "TR-UZ-2024-1-1") @PathVariable("id") String id) {
        log.info("Revoke permit request. {}", id);
        permitService.revokePermit(id);
    }

    @PostMapping("/{id}/activities")
    @Operation(summary = "Add permit activity", description = "Add permit activity by permit id")
    public void setUsed(
            @Parameter(description = "Permit Identifier", example = "TR-UZ-2024-1-1") @PathVariable("id") String id,
            @RequestBody @Valid PermitUsedInput input) {
        log.info("Permit used request. {}, {}", id, input);
        permitService.permitUsed(id, input);
    }
}
