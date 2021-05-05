package epermit.commands.revokepermit;

import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.IssuedPermit;
import epermit.events.AppEventPublisher;
import epermit.events.permitrevoked.PermitRevokedEvent;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.repositories.IssuedPermitRepository;
import epermit.services.EventService;
import lombok.SneakyThrows;

public class RevokePermitCommandHandler
        implements Command.Handler<RevokePermitCommand, CommandResult> {
    private final IssuedPermitRepository repository;
    private final PermitRevokedEventFactory factory;
    private final AppEventPublisher eventPublisher;
    private final EventService eventService;

    public RevokePermitCommandHandler(IssuedPermitRepository repository,
    AppEventPublisher eventPublisher, PermitRevokedEventFactory factory, EventService eventService) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.factory = factory;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(RevokePermitCommand cmd) {
        IssuedPermit permit = repository.findOneByPermitId(cmd.getPermitId()).get();
        permit.setRevoked(true);
        repository.save(permit);
        PermitRevokedEvent event = factory.create(permit);
        CreatedEvent e = eventService.persist(event);
        eventPublisher.publish(e);
        CommandResult result = CommandResult.success();
        return result;
    }
}
