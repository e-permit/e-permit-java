package epermit.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.models.dtos.PermitDto;
import epermit.services.PermitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {
    private final PermitService permitService;

    @GetMapping("/{qrCode}")
    @Operation(summary = "Verify permit", description = "Find permit by specified qr code")
    public PermitDto verify(
            @Parameter(description = "Permit Identifier", example = "TR-UZ-2024-1-1") @PathVariable("qrCode") String qrCode) {
        return permitService.getByQrCode(qrCode);
    }
}
