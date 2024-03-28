package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Key;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface KeyRepository extends JpaRepository<Key, UUID> {
    @Query(value = "SELECT count(*)>0  FROM epermit_keys where key_id=:keyId", nativeQuery = true)
    boolean existsByKeyId(@Param("keyId") String keyId);

    Optional<Key> findOneByKeyId(String keyId);
}