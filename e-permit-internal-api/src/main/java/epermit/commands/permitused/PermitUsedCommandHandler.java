package epermit.commands.permitused;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.Permit;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.repositories.PermitRepository;
import lombok.SneakyThrows;

public class PermitUsedCommandHandler implements Command.Handler<PermitUsedCommand, CommandResult> {
    private final PermitRepository repository;
    private final PermitUsedEventFactory factory;
    private final AppEventPublisher eventPublisher;

    public PermitUsedCommandHandler(AppEventPublisher eventPublisher, PermitRepository repository,
            PermitUsedEventFactory factory) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.factory = factory;
    }

    @Override
    @Transactional
    @SneakyThrows    public CommandResult handle(PermitUsedCommand cmd) {
        Permit permit = repository.findById(cmd.getPermitId()).get();
        permit.setUsed(true);
        repository.save(permit);
        CreatedEvent event = factory.create(permit);
        eventPublisher.publish(event);
        CommandResult result = CommandResult.success();
        return result;
    }
}
