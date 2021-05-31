package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
   Authority findOneByCode(String code);
}
