package epermit.events.keycreated;

import java.util.ArrayList;
import java.util.List;
import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.events.EventFactoryBase;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.entities.Key;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public class KeyCreatedEventFactory extends EventFactoryBase {

    private final AuthorityRepository authorityRepository;

    public KeyCreatedEventFactory(PermitProperties props, AuthorityRepository authorityRepository,
            CreatedEventRepository createdEventRepository, KeyService keyService) {
        super(props, createdEventRepository, keyService);
        this.authorityRepository = authorityRepository;
    }

    public List<CreatedEvent> create(Key key) {
        List<CreatedEvent> list = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(aud -> {
            KeyCreatedEvent event = KeyCreatedEvent.builder().keyId(key.getKid()).jwk(key.getContent()).build();
            setCommonClaims(event, aud.getCode(), EventType.KEY_CREATED);
            list.add(persist(event));
        });
        return list;
    }
}