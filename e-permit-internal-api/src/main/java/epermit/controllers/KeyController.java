package epermit.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.services.KeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
public class KeyController {
    private final KeyService keyService;

    @PostMapping()
    public void create(@RequestBody Map<String, String> input) {
        log.info("Key create request. {}", input);
        keyService.create(input.get("key_id"));
    }

    @PatchMapping("/{id}/enable")
    public void enable(@PathVariable("id") Integer id) {
        log.info("Key enable request. {}", id);
        keyService.enable(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        log.info("Key delete request. {}", id);
        keyService.delete(id);
    }
}
