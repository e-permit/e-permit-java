package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.Permit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PermitRepository extends JpaRepository<Permit, Long>, JpaSpecificationExecutor<Permit>{
    Optional<Permit> findOneByPermitId(String permitId);
    Optional<Permit> findOneByIssuerAndPermitId(String issuer, String permitId);
    boolean existsByIssuerAndPermitId(String issuer, String permitId);
}
