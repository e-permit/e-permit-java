package epermit.commands.permitused;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.Permit;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.permitused.PermitUsedEvent;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.repositories.PermitRepository;
import epermit.services.EventService;
import lombok.SneakyThrows;

public class PermitUsedCommandHandler implements Command.Handler<PermitUsedCommand, CommandResult> {
    private final PermitRepository repository;
    private final PermitUsedEventFactory factory;
    private final AppEventPublisher eventPublisher;
    private final EventService eventService;

    public PermitUsedCommandHandler(AppEventPublisher eventPublisher, PermitRepository repository,
            PermitUsedEventFactory factory,EventService eventService) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.factory = factory;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(PermitUsedCommand cmd) {
        Permit permit = repository.findOneByPermitId(cmd.getPermitId()).get();
        permit.setUsed(true);
        repository.save(permit);
        PermitUsedEvent event = factory.create(permit, cmd.getActivityType());
        CreatedEvent e = eventService.persist(event);
        eventPublisher.publish(e);
        CommandResult result = CommandResult.success();
        return result;
    }
}
