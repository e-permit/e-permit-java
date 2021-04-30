package epermit.commands.createauthority;

import java.time.OffsetDateTime;
import javax.transaction.Transactional;
import org.springframework.stereotype.Component;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.Authority;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

@Component
public class CreateAuthorityCommandHandler
        implements Command.Handler<CreateAuthorityCommand, CommandResult> {

    private final AuthorityRepository repository;

    public CreateAuthorityCommandHandler(AuthorityRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(CreateAuthorityCommand command) {
        Authority authority = new Authority();
        authority.setCode(command.getCode());
        authority.setName(command.getName());
        authority.setApiUri(command.getApiUri());
        authority.setVerifyUri(command.getVerifyUri());
        authority.setCreatedAt(OffsetDateTime.now());
        repository.save(authority);
        return CommandResult.success();
    }

}
