package epermit.controllers;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.AuthorityConfig;
import epermit.services.ConfigService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/epermit-configuration")
public class ConfigController {
    private final ConfigService configService;
    private final BuildProperties buildProperties;

    @GetMapping
    public AuthorityConfig getConfig() {
        return configService.getConfig();
    }

    @GetMapping("/version")
    public String getVersion() {
        return buildProperties.getVersion();
    }
}
