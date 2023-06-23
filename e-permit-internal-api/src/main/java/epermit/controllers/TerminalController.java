package epermit.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import epermit.entities.Terminal;
import epermit.models.inputs.CreateTerminalInput;
import epermit.repositories.TerminalRepository;

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
@RequestMapping("/terminals")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class TerminalController {
    private final TerminalRepository repository;

    @GetMapping()
    public List<Terminal> getAll() {
        return repository.findAll();
    }
    
    @PostMapping()
    public void create(@RequestBody @Valid CreateTerminalInput input) {
        log.info("Terminal create request. {}", input);
        createTerminal(input);
    }

    @Transactional
    void createTerminal(CreateTerminalInput input){
        Terminal terminal = new Terminal();
        terminal.setCode(input.getCode());
        terminal.setName(input.getName());
        repository.save(terminal);
    }
}
