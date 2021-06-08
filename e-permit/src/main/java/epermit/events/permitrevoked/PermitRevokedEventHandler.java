package epermit.events.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.entities.Permit;
import epermit.events.EventHandler;
import epermit.repositories.PermitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedEventHandler implements EventHandler {
    private final PermitRepository permitRepository;

    public void handle(Object e) {
        log.info("PermitRevokedEventHandler started with {}", e);
        PermitRevokedEvent event = (PermitRevokedEvent) e;
        Permit permit = permitRepository
                .findOneByIssuerAndPermitId(event.getIssuer(), event.getPermitId()).get();
        permitRepository.delete(permit);
    }
}
