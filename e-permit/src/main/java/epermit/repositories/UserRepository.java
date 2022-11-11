package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
   User findOneByUsername(String username);
}

