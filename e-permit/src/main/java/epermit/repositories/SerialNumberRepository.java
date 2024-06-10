package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.SerialNumber;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SerialNumberRepository
                extends JpaRepository<SerialNumber, UUID> {
        @Query("SELECT s FROM SerialNumber s WHERE s.permitIssuer = :issuer and " +
                        "s.permitIssuedFor = :issuedFor and s.permitType=:permitType and s.permitYear=:permitYear")
        Optional<SerialNumber> findOneByParams(@Param("issuer") String iss, @Param("issuedFor") String issFor,
                        @Param("permitType") Integer typ, @Param("permitYear") Integer year);
}
