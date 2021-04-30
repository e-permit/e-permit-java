package epermit.commands.createkey;

import javax.transaction.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.Key;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;
import lombok.SneakyThrows;

public class CreateKeyCommandHandler implements Command.Handler<CreateKeyCommand, CommandResult> {
    private final KeyRepository repository;
    private final KeyService keyService;

    public CreateKeyCommandHandler(KeyRepository repository, KeyService keyService){
        this.repository = repository;
        this.keyService = keyService;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(CreateKeyCommand cmd) {
        Key k = keyService.create(cmd.getKid());
        repository.save(k);
        return CommandResult.success();
    }

}
