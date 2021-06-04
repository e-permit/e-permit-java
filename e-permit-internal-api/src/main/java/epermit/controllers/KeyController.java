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

@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
public class KeyController {
    private final KeyService keyService;

    @PostMapping()
    public void create(@RequestBody Map<String, String> input) {
        keyService.create(input.get("key_id"));
    }

    @PatchMapping("/{id}/enable")
    public void enable(@PathVariable("id") Integer id) {
        keyService.enable(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        keyService.delete(id);
    }
}
