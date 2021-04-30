package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.AuthorityKey;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityKeyRepository extends JpaRepository<AuthorityKey, Integer> {
   
}
