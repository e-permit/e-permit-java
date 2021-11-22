package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.PrivateKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface PrivateKeyRepository extends JpaRepository<PrivateKey, UUID> {

    PrivateKey findFirstByEnabledTrueOrderByIdDesc();

    List<PrivateKey> findAllByEnabledTrue();

    Optional<PrivateKey> findOneByKeyId(String kid);

    boolean existsByKeyId(String keyId);
}
