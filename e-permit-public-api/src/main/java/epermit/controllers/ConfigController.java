package epermit.controllers;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.TrustedAuthority;
import epermit.services.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/epermit-configuration")
public class ConfigController {
    private final ConfigService configService;

    @GetMapping
    public AuthorityConfig getConfig() {
        log.info("ConfigController getConfig called");
        return configService.getConfig();
    }

    @GetMapping("/trusted_authorities")
    public List<TrustedAuthority> getAuthorities(){
        return configService.getTrustedAuthorities();
    }
}
