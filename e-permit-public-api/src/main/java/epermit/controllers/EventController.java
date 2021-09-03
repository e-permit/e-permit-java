package epermit.controllers;

import java.util.HashMap;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import epermit.commons.EpermitValidationException;
import epermit.commons.GsonUtil;
import epermit.ledgerevents.LedgerEventResult;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PostMapping("/permit-created")
    public LedgerEventResult permitCreated(@RequestHeader HttpHeaders headers,
            @Valid @RequestBody PermitCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, GsonUtil.toMap(event));
        return LedgerEventResult.success();
    }

    @PostMapping("/permit-used")
    public LedgerEventResult permitUsed(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid PermitUsedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
        return LedgerEventResult.success();
    }

    @PostMapping("/permit-revoked")
    public LedgerEventResult permitRevoked(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid PermitRevokedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
        return LedgerEventResult.success();
    }

    @PostMapping("/quota-created")
    public LedgerEventResult quotaCreated(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid QuotaCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
        return LedgerEventResult.success();
    }

    @PostMapping("/key-created")
    public LedgerEventResult keyCreated(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid KeyCreatedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
        return LedgerEventResult.success();
    }

    @PostMapping("/key-revoked")
    public LedgerEventResult keyRevoked(@RequestHeader HttpHeaders headers,
            @RequestBody @Valid KeyRevokedLedgerEvent event) {
        eventService.handleReceivedEvent(headers, event);
        return LedgerEventResult.success();
    }

    @ExceptionHandler({EpermitValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<LedgerEventResult> handleException(EpermitValidationException ex) {
        return ResponseEntity.badRequest().body(LedgerEventResult.fail(ex.getErrorCode()));
    }
}
