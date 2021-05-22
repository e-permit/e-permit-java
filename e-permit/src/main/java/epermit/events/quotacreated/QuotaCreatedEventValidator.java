package epermit.events.quotacreated;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("QUOTA_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class QuotaCreatedEventValidator implements EventValidator {
    @Override
    public EventValidationResult validate(Map<String, Object> payload) {
        QuotaCreatedEvent e = GsonUtil.fromMap(payload, QuotaCreatedEvent.class);

        return EventValidationResult.success(e);
    }

}
