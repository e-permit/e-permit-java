package epermit.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.AuthorityConfig;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/epermit-configuration")
public class ConfigController {
    private final AuthorityService authorityService;

    @GetMapping
    public AuthorityConfig getConfig() {
        return authorityService.getConfig();
    }
}
