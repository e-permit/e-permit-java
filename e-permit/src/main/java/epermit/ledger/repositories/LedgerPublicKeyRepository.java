package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerPublicKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerPublicKeyRepository extends JpaRepository<LedgerPublicKey, Integer> {
    Optional<LedgerPublicKey> findOneByAuthorityCodeAndKeyId(String code, String keyId);

    List<LedgerPublicKey> findAllByAuthorityCode(String code);
}
