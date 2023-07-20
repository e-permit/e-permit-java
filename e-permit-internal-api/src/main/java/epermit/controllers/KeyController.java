package epermit.controllers;

import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.services.PrivateKeyService;
import epermit.utils.PrivateKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
public class KeyController {
    private final PrivateKeyService keyService;
    private final PrivateKeyUtil keyUtil;

    @GetMapping()
    public String export(){
        return keyUtil.getKey().toJSONString();
    }

    @PostMapping()
    public void create(@RequestBody Map<String, String> input) {
        log.info("Key create request. {}", input);
        // Enable the key and disable all others  
        keyService.create(input.get("key_id"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") UUID id) {
        log.info("Key delete request. {}", id);
        keyService.delete(id);
    }
}
