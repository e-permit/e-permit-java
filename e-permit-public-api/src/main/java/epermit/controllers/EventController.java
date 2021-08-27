package epermit.controllers;

import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.commons.GsonUtil;
import epermit.ledgerevents.LedgerEventResult;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.services.PersistedEventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final PersistedEventService eventService;

    @PostMapping("permit-created")
    public LedgerEventResult permitCreated(@RequestHeader HttpHeaders headers,
            @Valid PermitCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }

    @PostMapping("permit-used")
    public LedgerEventResult permitUsed(@RequestHeader HttpHeaders headers,
            @Valid PermitUsedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }

    @PostMapping("permit-revoked")
    public LedgerEventResult permitRevoked(@RequestHeader HttpHeaders headers,
            @Valid PermitRevokedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }

    @PostMapping("quota-created")
    public LedgerEventResult quotaCreated(@RequestHeader HttpHeaders headers,
            @Valid QuotaCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }

    @PostMapping("key-created")
    public LedgerEventResult keyCreated(@RequestHeader HttpHeaders headers,
            @Valid KeyCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }

    @PostMapping("key-revoked")
    public LedgerEventResult keyRevoked(@RequestHeader HttpHeaders headers,
            @Valid KeyRevokedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }
}
