package epermit.controllers;

import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.services.EventService;
import epermit.services.PermitService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final PermitService permitService;

    @PostMapping("/permit-created")
    public void permitCreated(@RequestHeader HttpHeaders headers,
            @Valid @RequestBody PermitCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
    }

    @PostMapping("/permit-used")
    public void permitUsed(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid PermitUsedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
    }

    @PostMapping("/permit-locked")
    public void permitLocked(@RequestHeader HttpHeaders headers, @RequestBody String event) {
        permitService.handlePermitLocked(event);
    }

    @PostMapping("/permit-revoked")
    public void permitRevoked(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid PermitRevokedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
    }

    @PostMapping("/quota-created")
    public void quotaCreated(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid QuotaCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
    }

    @PostMapping("/key-created")
    public void keyCreated(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid KeyCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
    }

    @PostMapping("/key-revoked")
    public void keyRevoked(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid KeyRevokedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
    }
}
