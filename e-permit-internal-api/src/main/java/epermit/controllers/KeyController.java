package epermit.controllers;

import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.services.PrivateKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
@PreAuthorize("hasRole('ADMIN')")
public class KeyController {
    private final PrivateKeyService keyService;

    @PostMapping()
    public void create(@RequestBody Map<String, String> input) {
        log.info("Key create request. {}", input);
        keyService.create(input.get("key_id"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        log.info("Key delete request. {}", id);
        keyService.delete(id);
    }
}
