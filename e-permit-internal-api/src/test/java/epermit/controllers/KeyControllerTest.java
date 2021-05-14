package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.createkey.CreateKeyCommand;
import epermit.commands.enablekey.EnableKeyCommand;
import epermit.common.CommandResult;

@ExtendWith(MockitoExtension.class)
public class KeyControllerTest {
    @Mock
    Pipeline pipeline;
    @Test
    void createTest() {
        KeyController controller =
                new KeyController(pipeline);
        CreateKeyCommand cmd = new CreateKeyCommand();
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.create(cmd);
        assertTrue(r.isOk());
    }

    @Test
    void enableTest() {
        KeyController controller =
                new KeyController(pipeline);
        EnableKeyCommand cmd = new EnableKeyCommand();
        cmd.setId("TR");
        when(cmd.execute(pipeline)).thenReturn(CommandResult.success());
        CommandResult r = controller.enable(cmd);
        assertTrue(r.isOk());
    }
}
