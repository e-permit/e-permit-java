package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerPublicKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerPublicKeyRepository extends JpaRepository<LedgerPublicKey, UUID> {
    Optional<LedgerPublicKey> findOneByPartnerAndKeyId(String code, String keyId);

    boolean existsByPartnerAndKeyId(String code, String keyId);

    List<LedgerPublicKey> findAllByPartnerAndRevokedFalse(String code);
}
