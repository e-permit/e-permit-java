package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Authority;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
   Authority findOneByCode(String code);
}

