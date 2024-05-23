package epermit.controllers;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epermit.models.dtos.AuthorityConfig;
import epermit.services.ConfigService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class IndexController {
    private final BuildProperties buildProperties;
    private final ConfigService configService;

    @GetMapping
    public AuthorityConfig getConfig() {
        AuthorityConfig config = configService.getConfig();
        config.setVersion(buildProperties.getVersion());
        return config;
    }
    
    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
    }
}