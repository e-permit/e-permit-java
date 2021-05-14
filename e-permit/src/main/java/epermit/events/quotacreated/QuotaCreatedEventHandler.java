package epermit.events.quotacreated;

import org.springframework.stereotype.Service;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.services.AuthorityService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service("QUOTA_CREATED")
@RequiredArgsConstructor
public class QuotaCreatedEventHandler implements EventHandler {
    private final AuthorityService authorityService;
    @SneakyThrows
    public EventHandleResult handle(String payload) {
        QuotaCreatedEvent e = GsonUtil.getGson().fromJson(payload, QuotaCreatedEvent.class);
        authorityService.handleQuotaCreated(e);
        return EventHandleResult.success();
    }
}
