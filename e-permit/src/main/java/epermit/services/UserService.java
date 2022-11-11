package epermit.services;

import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import epermit.models.inputs.CreateUserInput;
import epermit.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Value("${epermit.admin-password}")
    private String adminPassword;

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username.equals("admin")) {
            return User.withUsername("admin").password("{noop}" + adminPassword).roles("ADMIN")
                    .build();
        }
        epermit.entities.User user = userRepository.findOneByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        UserBuilder builder =
                User.withUsername(user.getUsername()).password("{bcrypt}" + user.getPassword());
        if (user.getRole().equals("VERIFIER")) {
            if (user.getTerminal().isEmpty()) {
                throw new UsernameNotFoundException(
                        "Terminal should not be empty for verifier role");
            }
            builder.authorities("ROLE_VERIFIER", "TERMINAL_" + user.getTerminal());
        } else {
            builder.authorities("ROLE_" + user.getRole());
        }
        return builder.build();
    }

    @Transactional
    public String register(CreateUserInput input) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[16];
        random.nextBytes(bytes);
        String pwd = Base64.getUrlEncoder().encodeToString(bytes);
        String encodedPwd = encoder.encode(pwd);
        epermit.entities.User user = new epermit.entities.User();
        user.setPassword(encodedPwd);
        user.setRole(input.getRole());
        user.setUsername(input.getUsername());
        if (input.getRole().equals("VERIFIER")) {
            user.setTerminal(input.getTerminal().get());
        }
        userRepository.save(user);
        return pwd;
    }
}

