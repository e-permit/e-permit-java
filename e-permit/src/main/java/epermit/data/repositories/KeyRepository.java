package epermit.data.repositories;

import org.springframework.stereotype.Repository;
import epermit.data.entities.Key;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface KeyRepository extends JpaRepository<Key, Integer> {
     Optional<Key> findOneByActiveTrue();

     List<Key> findAllByActiveTrue();

     Optional<Key> findOneByKeyId(String kid);
}

