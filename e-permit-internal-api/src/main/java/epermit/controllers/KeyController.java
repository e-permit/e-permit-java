package epermit.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.models.dtos.KeyDto;
import epermit.services.KeyService;
import epermit.utils.PrivateKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
public class KeyController {
    private final KeyService keyService;
    private final PrivateKeyUtil keyUtil;

    @GetMapping()
    public String export(){
        return "";
        //return keyUtil.getKey().toJSONString();
    }

    @PostMapping()
    public void create(@RequestBody Map<String, String> input) {
        log.info("Key create request. {}", input);
        KeyDto createKeyInput = keyUtil.create(input.get("key_id"));
        keyService.create(createKeyInput);
    }

    @DeleteMapping("/{keyId}")
    public void delete(@PathVariable("keyId") String keyId) {
        log.info("Key delete request. {}", keyId);
        keyService.revoke(keyId);
    }
}
