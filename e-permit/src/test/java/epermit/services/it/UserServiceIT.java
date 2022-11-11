package epermit.services.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.models.inputs.CreateUserInput;
import epermit.services.UserService;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceIT {
    @Autowired
    private UserService userService;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    @Test
    void permitCreatedTest() {
        CreateUserInput input = new CreateUserInput();
        input.setUsername("adem");
        input.setRole("VERIFIER");
        input.setTerminal(Optional.of("EDIRNE"));
        String pwd = userService.register(input);
        UserDetails u = userService.loadUserByUsername("adem");
        assertEquals(new BCryptPasswordEncoder().encode(pwd), u.getPassword());
    }
}
