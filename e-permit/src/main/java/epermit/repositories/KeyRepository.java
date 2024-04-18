package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Key;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {
    Optional<Key> findOneByKeyId(String keyId);
    Optional<Key> findFirstByRevokedFalseOrderByCreatedAtDesc();
    Optional<Key> findOneByKeyIdAndRevokedFalse(String keyId);
}