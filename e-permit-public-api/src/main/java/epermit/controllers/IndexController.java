package epermit.controllers;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class IndexController {
    private final BuildProperties buildProperties;

    @GetMapping()
    public String getVersion() {
        return buildProperties.getVersion();
    }
}