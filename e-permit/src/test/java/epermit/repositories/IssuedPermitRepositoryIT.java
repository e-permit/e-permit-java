package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import epermit.entities.IssuedPermit;
import epermit.models.enums.PermitType;

@DataJpaTest
public class IssuedPermitRepositoryIT {
    @Autowired
    private IssuedPermitRepository repository;

    @Test
    void saveTest(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        IssuedPermit permit = new IssuedPermit();       
        permit.setCompanyName("c");
        permit.setIssuedFor("UZ");
        permit.setPermitId("TR-UZ-2021-1-1");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06");
        permit.setSerialNumber(1);
        permit.setIssuedAt(OffsetDateTime.now().format(dtf));
        permit.setExpireAt("30/01/" + Integer.toString(2022));
        permit.setQrCode("ey...");
        repository.save(permit);
        IssuedPermit issuedPermit = repository.findOneByIssuedForAndPermitId("UZ", "TR-UZ-2021-1-1").get();
        assertEquals(permit.getCreatedAt(), issuedPermit.getCreatedAt());
        //Example<IssuedPermit> example = Example.of(permit);
        //assertTrue(repository.exists(example));
    }
}
