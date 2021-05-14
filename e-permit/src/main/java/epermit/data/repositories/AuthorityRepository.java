package epermit.data.repositories;


import org.springframework.stereotype.Repository;
import epermit.data.entities.Authority;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findOneByCode(String code);
}
