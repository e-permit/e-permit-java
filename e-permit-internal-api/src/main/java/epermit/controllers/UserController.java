package epermit.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import epermit.entities.User;
import epermit.models.inputs.CreateUserInput;
import epermit.repositories.UserRepository;
import epermit.services.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/users")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping()
    public List<User> getAll() {
        return userRepository.findAll();
    }
    
    @PostMapping()
    public void createUser(@RequestBody @Valid CreateUserInput input) {
        log.info("User create request. {}", input);
        userService.register(input);
    }
}
