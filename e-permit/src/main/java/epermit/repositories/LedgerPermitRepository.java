package epermit.repositories;

import org.springframework.stereotype.Repository;

import epermit.entities.LedgerPermit;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface LedgerPermitRepository
          extends JpaRepository<LedgerPermit, UUID>, JpaSpecificationExecutor<LedgerPermit> {
     Optional<LedgerPermit> findOneByPermitId(String permitId);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<LedgerPermit> findOneForUpdateByPermitId(String code);

     Optional<LedgerPermit> findOneByQrCode(String qrCode);

     boolean existsByPermitId(String permitId);
}