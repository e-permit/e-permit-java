package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.IssuedPermit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IssuedPermitRepository extends JpaRepository<IssuedPermit, Long> {
     Optional<IssuedPermit> findOneBySerialNumber(String serialNumber);

     //Optional<IssuedPermit> findFirstByPermitTypeAndPermitYearOrderByPermitIdDesc();

     Optional<IssuedPermit> findFirstByRevokedTrue();
}
