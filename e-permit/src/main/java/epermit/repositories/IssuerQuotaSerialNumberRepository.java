package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.IssuerQuotaSerialNumber;
import epermit.models.enums.PermitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface IssuerQuotaSerialNumberRepository
        extends JpaRepository<IssuerQuotaSerialNumber, Integer> {

        @Query("")
        IssuerQuotaSerialNumber getSerialNumber(String issuer, String issuedFor, PermitType permitType, Integer permitYear);
}

