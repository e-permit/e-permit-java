package epermit.controllers;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
import epermit.services.PersistedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final PersistedEventService eventService;

    @PostMapping("permit-created")
    public LedgerEventResult permitCreated(@RequestHeader HttpHeaders headers,
            @Valid PermitCreatedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-used")
    public LedgerEventResult permitUsed(@RequestHeader HttpHeaders headers,
            @Valid PermitUsedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-revoked")
    public LedgerEventResult permitRevoked(@RequestHeader HttpHeaders headers,
            @Valid PermitRevokedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("quota-created")
    public LedgerEventResult quotaCreated(@RequestHeader HttpHeaders headers,
            @Valid QuotaCreatedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("key-created")
    public LedgerEventResult keyCreated(@RequestHeader HttpHeaders headers,
            @Valid KeyCreatedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("key-revoked")
    public LedgerEventResult keyRevoked(@RequestHeader HttpHeaders headers,
            @Valid KeyRevokedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    private LedgerEventResult getResult(HttpHeaders headers, Map<String, Object> claims) {
        log.info("Event claims. {}", claims);
        String proof = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Event jws. {}", proof);
        try {
            eventService.handleReceivedEvent(claims, proof);
            return LedgerEventResult.success();
        } catch (EpermitValidationException ex) {
            return LedgerEventResult.fail(ex.getErrorCode(), ex.getMessage());
        }

    }
}
