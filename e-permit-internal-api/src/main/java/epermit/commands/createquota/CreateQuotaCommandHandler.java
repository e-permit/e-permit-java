package epermit.commands.createquota;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.Authority;
import epermit.entities.VerifierQuota;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

@Component
public class CreateQuotaCommandHandler
        implements Command.Handler<CreateQuotaCommand, CommandResult> {

    private final AuthorityRepository repository;

    public CreateQuotaCommandHandler(AuthorityRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(CreateQuotaCommand command) {
        Authority authority = repository.findByCode(command.getAuthorityCode()).get();
        VerifierQuota quota = new VerifierQuota();
        quota.setAuthority(authority);
        quota.setPermitType(command.getPermitType());
        quota.setEndNumber(command.getEndId());
        quota.setStartNumber(command.getStartId());
        quota.setPermitYear(command.getPermitYear());
        quota.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        authority.addVerifierQuota(quota);
        repository.save(authority);
        return CommandResult.success();
    }
}

