package epermit.commands.enablekey;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import an.awesome.pipelinr.Command;
import epermit.common.CommandResult;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.entities.Key;
import epermit.events.AppEvent;
import epermit.events.AppEventPublisher;
import epermit.events.keycreated.KeyCreatedEvent;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.services.EventService;
import lombok.SneakyThrows;

public class EnableKeyCommandHandler implements Command.Handler<EnableKeyCommand, CommandResult> {
    private final KeyCreatedEventFactory factory;
    private final KeyRepository repository;
    private final AuthorityRepository authorityRepository;
    private final AppEventPublisher eventPublisher;
    private final EventService eventService;

    public EnableKeyCommandHandler(KeyRepository repository, AuthorityRepository authorityRepository,
            KeyCreatedEventFactory factory, AppEventPublisher eventPublisher, EventService eventService) {
        this.repository = repository;
        this.factory = factory;
        this.eventPublisher = eventPublisher;
        this.authorityRepository = authorityRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    @SneakyThrows
    public CommandResult handle(EnableKeyCommand cmd) {
        Key key = repository.findOneByKid(cmd.getKeyId()).get();
        key.setEnabled(true);
        repository.save(key);
        List<Authority> authorities = authorityRepository.findAll();  
        authorities.forEach(aud -> {
            KeyCreatedEvent event = factory.create(key, aud.getCode());
            CreatedEvent e = eventService.persist(event);
            eventPublisher.publish(e);
        });
        CommandResult result = CommandResult.success();
        return result;
    }
}
