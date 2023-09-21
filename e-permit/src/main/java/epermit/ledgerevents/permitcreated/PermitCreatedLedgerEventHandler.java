package epermit.ledgerevents.permitcreated;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.dtos.CreatePermitIdDto;
import epermit.models.dtos.QuotaSufficientDto;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.PermitUtil;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class PermitCreatedLedgerEventHandler implements LedgerEventHandler {

    private final LedgerPermitRepository permitRepository;

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
        Boolean isQuotaSufficient = permitUtil.isQuotaSufficient(getQuotaSufficientInput(event));
        if (!isQuotaSufficient)
            throw new EpermitValidationException(ErrorCodes.INSUFFICIENT_PERMIT_QUOTA);
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
        permit.setQrCode(event.getQrCode());
        if (event.getOtherClaims() != null && !event.getOtherClaims().isEmpty()) {
            permit.setOtherClaims(GsonUtil.getGson().toJson(event.getOtherClaims()));
        }
        log.info("PermitCreatedEventFactory ended with {}", permit);
        permitRepository.save(permit);
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

    private QuotaSufficientDto getQuotaSufficientInput(PermitCreatedLedgerEvent event) {
        QuotaSufficientDto input = new QuotaSufficientDto();
        input.setIssuedFor(event.getEventConsumer());
        input.setIssuer(event.getEventProducer());
        input.setPermitType(event.getPermitType());
        input.setPermitYear(event.getPermitYear());
        input.setSerialNumber(event.getSerialNumber());
        return input;
    }
}
