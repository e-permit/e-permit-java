package epermit.controllers;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.models.dtos.PermitDto;
import epermit.services.PermitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/verify")
public class VerifyController {
    private final PermitService permitService;

    @GetMapping("/{qrCode}")
    @Operation(summary = "Verify permit", description = "Find permit by specified qr code")
    public PermitDto verify(
            @Parameter(description = "Permit Qr Code", example = "ey...") @PathVariable("qrCode") String qrCode) {
        MDC.put("qrCode", qrCode);
        log.info("Qr Code verify request: {}", qrCode);
        MDC.remove("qrCode");
        return permitService.getByQrCode(qrCode);
    }
}
