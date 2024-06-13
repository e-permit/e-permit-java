package epermit.ledgerevents.permitcreated;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.PermitId;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitCreatedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;
    private final LedgerQuotaRepository quotaRepository;

    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("PermitCreatedEventHandler started with {}", claims);
        PermitCreatedLedgerEvent event = (PermitCreatedLedgerEvent) claims;
        boolean exist = permitRepository.existsByPermitId(event.getPermitId());
        if (exist)
            throw new EpermitValidationException(ErrorCodes.PERMITID_ALREADY_EXISTS);

        PermitId permitId = PermitId.parse(event.getPermitId());
        LedgerQuota quota = quotaRepository.findOneByParams(event.getPermitIssuer(),
                event.getPermitIssuedFor(), event.getPermitType(), event.getPermitYear())
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA));
        if (permitId.getSerialNumber() > quota.getBalance()) {
            throw new EpermitValidationException(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA);
        }

        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId(event.getCompanyId());
        permit.setCompanyName(event.getCompanyName());
        permit.setExpireAt(event.getExpireAt());
        permit.setIssuedAt(event.getIssuedAt());
        permit.setIssuer(event.getEventProducer());
        permit.setIssuedFor(event.getEventConsumer());
        permit.setPermitId(event.getPermitId());
        permit.setPermitType(event.getPermitType());
        permit.setPermitYear(event.getPermitYear());
        permit.setPlateNumber(event.getPlateNumber());
        permit.setPlateNumber2(event.getPlateNumber2());
        permit.setDepartureCountry(event.getDepartureCountry());
        permit.setArrivalCountry(event.getArrivalCountry());
        permit.setQrCode(event.getQrCode());
        if (event.getOtherClaims() != null && !event.getOtherClaims().isEmpty()) {
            permit.setOtherClaims(GsonUtil.getGson().toJson(event.getOtherClaims()));
        }
        log.info("PermitCreatedEventFactory ended with {}", permit);
        permitRepository.save(permit);
    }
}
