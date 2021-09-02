package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface SerialNumberRepository
        extends JpaRepository<SerialNumber, Integer>, JpaSpecificationExecutor<SerialNumber> {

}

