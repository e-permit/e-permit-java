package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Key;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface KeyRepository extends JpaRepository<Key, Integer> {

     Key findFirstByEnabledTrueOrderByIdDesc();

     List<Key> findAllByEnabledTrue();

     Optional<Key> findOneByKeyId(String kid);

     boolean existsByKeyId(String keyId);
}

