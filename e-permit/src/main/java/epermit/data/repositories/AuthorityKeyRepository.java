package epermit.data.repositories;


import org.springframework.stereotype.Repository;
import epermit.data.entities.AuthorityKey;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityKeyRepository extends JpaRepository<AuthorityKey, Integer> {
   
}
