package epermit.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Tag(name = "Events", description = "Created Event APIs")
public class EventController {
    private final CreatedEventRepository createdEventRepository;

    @GetMapping()
    @Operation(summary = "Get events", description = "Returns pending created events.")
    public List<CreatedEvent> getAll() {
        return createdEventRepository.findAllBySentFalseOrderByCreatedAtAsc();
    }
}
