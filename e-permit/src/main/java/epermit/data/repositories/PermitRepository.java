package epermit.data.repositories;


import org.springframework.stereotype.Repository;
import epermit.data.entities.Permit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PermitRepository extends JpaRepository<Permit, Long> {
    Optional<Permit> findOneByPermitId(String permitId);

    //Optional<Permit> findFirstByOrderByPermitIdDesc();

    Optional<Permit> findFirstByRevokedTrue();
}
