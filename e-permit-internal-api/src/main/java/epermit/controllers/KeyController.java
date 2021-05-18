package epermit.controllers;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.results.CommandResult;
import epermit.services.KeyService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keys")
public class KeyController {
    private final KeyService keyService;

    @PostMapping()
    public ResponseEntity<CommandResult> create(@RequestBody Map<String, String> input) {
        CommandResult r = keyService.create(input.get("key_id"));
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("{id}/enable")
    public ResponseEntity<CommandResult> enable(@RequestParam Integer id) {
        CommandResult r = keyService.enable(id);
        if (r.isOk()) {
            return new ResponseEntity<CommandResult>(r, HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<CommandResult>(r, HttpStatus.BAD_REQUEST);
        }
    }
}
