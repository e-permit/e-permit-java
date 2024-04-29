package epermit.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.models.dtos.KeyDto;
import epermit.services.KeyService;
import epermit.utils.PrivateKeyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
@Tag(name = "Keys", description = "Key Management APIs")
public class KeyController {
    private final KeyService keyService;

    @PostMapping("/{keyId}")
    @Operation(summary = "Create key", description = "Create new key to sign events")
    public void create(@Parameter(description = "Key Identifier", example = "1") @PathVariable("keyId") String keyId) {
        log.info("Key create request. {}", keyId);
        keyService.create(keyId);
    }

    @DeleteMapping("/{keyId}")
    @Operation(summary = "Revoke key", description = "Revoke key")
    public void revoke(@Parameter(description = "Key Identifier", example = "1") @PathVariable("keyId") String keyId) {
        log.info("Key delete request. {}", keyId);
        keyService.revoke(keyId);
    }
}
