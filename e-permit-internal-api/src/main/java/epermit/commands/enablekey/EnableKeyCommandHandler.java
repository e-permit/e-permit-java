package epermit.commands.enablekey;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.CreatedEvent;
import epermit.entities.Key;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.repositories.KeyRepository;
import lombok.SneakyThrows;

public class EnableKeyCommandHandler implements Command.Handler<EnableKeyCommand, CommandResult> {
    private final KeyCreatedEventFactory factory;
    private final KeyRepository repository;
    private final AppEventPublisher eventPublisher;

    public EnableKeyCommandHandler(KeyRepository repository, KeyCreatedEventFactory factory,
            AppEventPublisher eventPublisher) {
        this.repository = repository;
        this.factory = factory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(EnableKeyCommand cmd) {
        Key key = repository.findOneByKid(cmd.getKeyId()).get();
        key.setEnabled(true);
        repository.save(key);
        List<CreatedEvent> events = factory.create(key);
        events.forEach(x -> eventPublisher.publish(x));
        CommandResult result = CommandResult.success();
        return result;
    }
}
