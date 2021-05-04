package epermit.events.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.entities.Permit;
import epermit.repositories.PermitRepository;
import lombok.SneakyThrows;

@Service("PERMIT_REVOKED")
public class PermitRevokedEventHandler {
    private final PermitRepository repository;
    private final PermitRevokedEventValidator validator;

    public PermitRevokedEventHandler(PermitRepository repository, PermitRevokedEventValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @SneakyThrows
    public void handle(PermitRevokedEvent event) {
        Boolean valid = validator.validate(event);
        if (valid) {
            Permit p = repository.findOneByPermitId(event.getPermitId()).get();
            repository.delete(p);
        }
    }
}