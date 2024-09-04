package epermit.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@RestController
@RequiredArgsConstructor
@RequestMapping("/info")
public class InfoController {
    @GetMapping
    @SneakyThrows
    @Operation(summary = "Verify permit", description = "Find permit by specified qr code")
    public Map<String, Object> get_info(Map<String, Object> params) {
        throw new Exception("");
    }
}
