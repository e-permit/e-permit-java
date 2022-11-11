package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.Terminal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, UUID> {
   Terminal findOneByCode(String code);
}

