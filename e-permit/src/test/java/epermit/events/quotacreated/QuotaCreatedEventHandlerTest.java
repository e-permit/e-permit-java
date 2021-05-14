package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventHandleResult;
import epermit.models.PermitType;
import epermit.services.AuthorityService;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventHandlerTest {
        @Mock
        AuthorityService authorityService;

        @InjectMocks
        QuotaCreatedEventHandler handler;
    
        @Test
        void handleTest() {
            QuotaCreatedEvent event =  new QuotaCreatedEvent();
            event.setIssuer("TR");
            event.setIssuedFor("UA");
            event.setStartNumber(4);
            event.setEndNumber(40);
            event.setPermitType(PermitType.BILITERAL);
            event.setPermitYear(4);
            EventHandleResult r = handler.handle(GsonUtil.getGson().toJson(event));
            assertTrue(r.isOk());
            verify(authorityService, times(1)).handleQuotaCreated(event);
        }
}
