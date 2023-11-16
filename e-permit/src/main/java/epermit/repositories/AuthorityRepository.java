package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Authority;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
   Optional<Authority> findOneByCode(String code);
   Optional<Authority> findOneByApiUri(String apiUri);
}

