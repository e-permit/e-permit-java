package epermit.events.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.entities.Permit;
import epermit.events.EventHandler;
import epermit.repositories.PermitRepository;
import lombok.RequiredArgsConstructor;

@Service("PERMIT_REVOKED_HANDLER")
@RequiredArgsConstructor
public class PermitRevokedEventHandler implements EventHandler {
    private final PermitRepository permitRepository;

    public void handle(Object e) {
        PermitRevokedEvent event = (PermitRevokedEvent) e;
        Permit permit = permitRepository
                .findOneByIssuerAndPermitId(event.getIssuer(), event.getPermitId()).get();
        permitRepository.delete(permit);
    }
}
