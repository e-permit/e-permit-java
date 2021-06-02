package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;
import epermit.entities.Permit;
import epermit.models.enums.PermitType;

@DataJpaTest
public class PermitRepositoryIT {
    @Autowired
    private PermitRepository repository;

    @Test
    void saveTest(){
        Permit p = save();
        Example<Permit> example = Example.of(p);
        assertTrue(repository.exists(example));
    }

    @Transactional
    private Permit save(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Permit permit = new Permit();       
        permit.setCompanyName("c");
        permit.setIssuer("UZ");
        permit.setPermitId("TR-UZ-2021-1-1");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06");
        permit.setSerialNumber(1);
        permit.setIssuedAt(OffsetDateTime.now().format(dtf));
        permit.setExpireAt("30/01/" + Integer.toString(2022));
        return repository.save(permit);
    }
    
}

