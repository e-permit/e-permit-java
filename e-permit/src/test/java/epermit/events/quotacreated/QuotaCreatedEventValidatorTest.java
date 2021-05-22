package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventValidationResult;
import epermit.models.enums.PermitType;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventValidatorTest {
    @InjectMocks
    QuotaCreatedEventValidator validator;

    @Test
    void handleTest() {
        QuotaCreatedEvent event = new QuotaCreatedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setStartNumber(4);
        event.setEndNumber(40);
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(4);
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertTrue(r.isOk());
    }
}
