package epermit.controllers;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.createkey.CreateKeyCommand;
import epermit.commands.enablekey.EnableKeyCommand;
import epermit.common.CommandResult;

@RestController
@RequestMapping("/keys")
public class KeyController {
    private final Pipeline pipeline;

    public KeyController(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    
    @PostMapping()
    public CommandResult create(@RequestBody CreateKeyCommand command) {
        return command.execute(pipeline);
    }

    @PatchMapping("{id}/enable")
    public CommandResult enable(@RequestParam String keyId) {
        EnableKeyCommand cmd = new EnableKeyCommand();
        cmd.setKeyId(keyId);
        return cmd.execute(pipeline);
    }
}
