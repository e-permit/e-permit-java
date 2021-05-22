package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.AuthorityKey;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AuthorityKeyRepository extends JpaRepository<AuthorityKey, Integer> {
    @Query("SELECT COUNT(ak) > 0 FROM AuthorityKey ak WHERE ak.authority.code = :authorityCode and ak.keyId = :keyId")
    boolean isPublicKeyExist(String authorityCode, String keyId);

    @Query("SELECT ak FROM AuthorityKey ak WHERE ak.authority.code = :authorityCode and ak.keyId = :keyId")
    Optional<AuthorityKey> findOneByIssuerAndKeyId(String authorityCode, String keyId);
}
