package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.JsonUtil;
import epermit.common.PermitType;
import epermit.entities.Authority;
import epermit.events.EventHandleResult;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventHandlerTest {
        @Mock
        AuthorityRepository authorityRepository;
    
        @Test
        void handleShouldWork() {
            Authority authority = new Authority();
            when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.of(authority));
            QuotaCreatedEventHandler handler = new QuotaCreatedEventHandler(authorityRepository);
            QuotaCreatedEvent event =  QuotaCreatedEvent.builder().endNumber(4).startNumber(1).permitType(PermitType.BILITERAL)
                    .permitYear(2021).build();
            event.setIssuer("TR");
            event.setIssuedFor("UA");
            String payload = JsonUtil.getGson().toJson(event);
            EventHandleResult r = handler.handle(payload);
            assertTrue(r.isSucceed());
        }
}
