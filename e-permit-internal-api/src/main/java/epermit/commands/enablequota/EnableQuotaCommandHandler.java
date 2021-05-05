package epermit.commands.enablequota;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.VerifierQuota;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.quotacreated.QuotaCreatedEvent;
import epermit.events.quotacreated.QuotaCreatedEventFactory;
import epermit.repositories.VerifierQuotaRepository;
import epermit.services.EventService;
import lombok.SneakyThrows;

public class EnableQuotaCommandHandler implements Command.Handler<EnableQuotaCommand, CommandResult> {
    private final VerifierQuotaRepository repository;
    private final QuotaCreatedEventFactory factory;
    private final AppEventPublisher eventPublisher;
    private final EventService eventService;

    public EnableQuotaCommandHandler(AppEventPublisher eventPublisher, VerifierQuotaRepository repository,
            QuotaCreatedEventFactory factory, EventService eventService) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.factory = factory;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(EnableQuotaCommand cmd) {
        VerifierQuota quota = repository.findById(cmd.getQuotaId()).get();
        quota.setActive(true);
        repository.save(quota);
        QuotaCreatedEvent event = factory.create(quota);
        CreatedEvent e = eventService.persist(event);
        eventPublisher.publish(e);
        CommandResult result = CommandResult.success();
        return result;
    }
}
