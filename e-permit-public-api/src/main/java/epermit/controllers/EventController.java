package epermit.controllers;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.commons.EpermitValidationException;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.services.PersistedEventService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final PersistedEventService eventService;

    @PostMapping("permit-created")
    public LedgerEventHandleResult permitCreated(@RequestHeader HttpHeaders headers,
            @Valid PermitCreatedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-used")
    public LedgerEventHandleResult permitUsed(@RequestHeader HttpHeaders headers,
            @Valid PermitUsedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-created")
    public LedgerEventHandleResult permitRevokedc(@RequestHeader HttpHeaders headers,
            @Valid PermitRevokedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-created")
    public LedgerEventHandleResult quotaCreated(@RequestHeader HttpHeaders headers,
            @Valid QuotaCreatedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-created")
    public LedgerEventHandleResult keyCreated(@RequestHeader HttpHeaders headers,
            @Valid KeyCreatedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    @PostMapping("permit-created")
    public LedgerEventHandleResult keyRevoked(@RequestHeader HttpHeaders headers,
            @Valid KeyRevokedLedgerEvent event) {
        return getResult(headers, GsonUtil.toMap(event));
    }

    private LedgerEventHandleResult getResult(HttpHeaders headers, Map<String, Object> claims) {
        log.info("Event claims. {}", claims);
        String proof = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Event jws. {}", proof);
        try {
            LedgerEventHandleResult r = eventService.handleEvent(claims, proof);
            log.info("Receive event finished {}", r);
            return r;
        } catch (EpermitValidationException ex) {
            return LedgerEventHandleResult.fail(ex.getErrorCode());
        }
       
    }
}
