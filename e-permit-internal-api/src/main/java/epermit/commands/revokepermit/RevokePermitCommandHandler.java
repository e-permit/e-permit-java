package epermit.commands.revokepermit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.IssuedPermit;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.repositories.IssuedPermitRepository;
import lombok.SneakyThrows;

public class RevokePermitCommandHandler
        implements Command.Handler<RevokePermitCommand, CommandResult> {
    private final IssuedPermitRepository repository;
    private final PermitRevokedEventFactory factory;
    private final AppEventPublisher eventPublisher;

    public RevokePermitCommandHandler(IssuedPermitRepository repository,
    AppEventPublisher eventPublisher, PermitRevokedEventFactory factory) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.factory = factory;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(RevokePermitCommand cmd) {
        IssuedPermit permit = repository.findOneByPermitId(cmd.getPermitId()).get();
        permit.setRevoked(true);
        repository.save(permit);
        CreatedEvent event = factory.create(permit.getIssuedFor(), permit.getPermitId());
        eventPublisher.publish(event);
        CommandResult result = CommandResult.success();
        return result;
    }
}
