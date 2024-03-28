package epermit.ledgerevents.permitcreated;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.dtos.CreatePermitIdDto;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.utils.PermitUtil;
import epermit.utils.QuotaUtil;

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

    private final PermitUtil permitUtil;

    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("PermitCreatedEventHandler started with {}", claims);
        PermitCreatedLedgerEvent event = (PermitCreatedLedgerEvent) claims;
        String expectedPermitId = permitUtil.getPermitId(getCreatePermitIdInput(event));
        if (!expectedPermitId.equals(event.getPermitId()))
            throw new EpermitValidationException(ErrorCodes.INVALID_PERMITID);
        boolean exist = permitRepository.existsByPermitId(event.getPermitId());
        if (exist)
            throw new EpermitValidationException(ErrorCodes.PERMITID_ALREADY_EXISTS);
        LedgerQuota quota = quotaRepository.findOne(QuotaUtil.filterQuotas(event.getPermitIssuer(),
                event.getPermitIssuedFor(), event.getPermitType(), event.getPermitYear()))
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA));
        if (quota.getBalance() <= 0) {
            throw new EpermitValidationException(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA);
        }
        quota.setBalance(quota.getBalance() - 1);
        quota.setNextSerial(quota.getNextSerial() + 1);
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
        permit.setSerialNumber(event.getSerialNumber());
        if (event.getOtherClaims() != null && !event.getOtherClaims().isEmpty()) {
            permit.setOtherClaims(GsonUtil.getGson().toJson(event.getOtherClaims()));
        }
        log.info("PermitCreatedEventFactory ended with {}", permit);
        permitRepository.save(permit);
        quotaRepository.save(quota);
    }

    private CreatePermitIdDto getCreatePermitIdInput(PermitCreatedLedgerEvent event) {
        CreatePermitIdDto input = new CreatePermitIdDto();
        input.setIssuedFor(event.getEventConsumer());
        input.setIssuer(event.getEventProducer());
        input.setPermitType(event.getPermitType());
        input.setPermitYear(event.getPermitYear());
        input.setSerialNumber(event.getSerialNumber());
        return input;
    }
}
