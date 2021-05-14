package epermit.data.repositories;


import org.springframework.stereotype.Repository;
import epermit.data.entities.IssuedPermit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IssuedPermitRepository extends JpaRepository<IssuedPermit, Long> {
     Optional<IssuedPermit> findOneByPermitId(String permitId);

     //Optional<IssuedPermit> findFirstByPermitTypeAndPermitYearOrderByPermitIdDesc();

     Optional<IssuedPermit> findFirstByRevokedTrue();
}
