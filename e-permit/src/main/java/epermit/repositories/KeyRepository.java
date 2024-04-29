package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Key;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {
    List<Key> findAllByRevokedFalse();
    Optional<Key> findOneByKeyId(String keyId);
    Optional<Key> findFirstByRevokedFalseOrderByCreatedAtAsc();
    Optional<Key> findOneByKeyIdAndRevokedFalse(String keyId);
}