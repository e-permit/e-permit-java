package epermit.commands.enablequota;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.VerifierQuota;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.quotacreated.QuotaCreatedEventFactory;
import epermit.repositories.VerifierQuotaRepository;
import lombok.SneakyThrows;

public class EnableQuotaCommandHandler implements Command.Handler<EnableQuotaCommand, CommandResult> {
    private final VerifierQuotaRepository repository;
    private final QuotaCreatedEventFactory factory;
    private final AppEventPublisher eventPublisher;

    public EnableQuotaCommandHandler(AppEventPublisher eventPublisher, VerifierQuotaRepository repository,
            QuotaCreatedEventFactory factory) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.factory = factory;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(EnableQuotaCommand cmd) {
        VerifierQuota quota = repository.findById(cmd.getQuotaId()).get();
        quota.setActive(true);
        repository.save(quota);
        CreatedEvent event = factory.create(quota);
        eventPublisher.publish(event);
        CommandResult result = CommandResult.success();
        return result;
    }
}
