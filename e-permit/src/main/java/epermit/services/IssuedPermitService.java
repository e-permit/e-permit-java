package epermit.services;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.IssuedPermit;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.models.EPermitProperties;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.inputs.CreatePermitInput;
import epermit.repositories.IssuedPermitRepository;
import epermit.utils.PermitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class IssuedPermitService {
    private final ModelMapper modelMapper;
    private final PermitUtil permitUtil;
    private final EPermitProperties properties;
    private final IssuedPermitRepository issuedPermitRepository;
    private final PermitCreatedEventFactory permitCreatedEventFactory;
    private final PermitRevokedEventFactory permitRevokedEventFactory;

    public IssuedPermitDto getById(Long id) {
        IssuedPermitDto dto =
                modelMapper.map(issuedPermitRepository.findById(id).get(), IssuedPermitDto.class);
        return dto;
    }

    public Page<IssuedPermitDto> getAll(String issuedFor, Pageable pageable) {
        Page<epermit.entities.IssuedPermit> entities = issuedPermitRepository.findAll(pageable);
        return entities.map(x -> modelMapper.map(x, IssuedPermitDto.class));
    }

    @Transactional
    public String createPermit(CreatePermitInput input) {
        log.info("Permit create started");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String issuer = properties.getIssuerCode();
        Integer serialNumber = permitUtil.generateSerialNumber(input.getIssuedFor(),
                input.getPermitYear(), input.getPermitType());
        if (serialNumber == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "INSUFFICIENT_SERIALNUMBER");
        }
        String permitId = permitUtil.getPermitId(issuer, input.getIssuedFor(),
                input.getPermitType(), input.getPermitYear(), serialNumber);
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName(input.getCompanyName());
        permit.setIssuedFor(input.getIssuedFor());
        permit.setPermitId(permitId);
        permit.setPermitType(input.getPermitType());
        permit.setPermitYear(input.getPermitYear());
        permit.setPlateNumber(input.getPlateNumber());
        permit.setSerialNumber(serialNumber);
        permit.setIssuedAt(OffsetDateTime.now().format(dtf));
        permit.setExpireAt("30/01/" + Integer.toString(input.getPermitYear() + 1));
        permit.setQrCode(permitUtil.generateQrCode(permit));
        issuedPermitRepository.save(permit);
        permitCreatedEventFactory.create(permit);
        log.info("Permit create finished");
        return permit.getPermitId();
    }

    @Transactional
    public void revokePermit(Long id, String comment) {
        Optional<IssuedPermit> permitOptional = issuedPermitRepository.findById(id);
        if (!permitOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PERMIT_NOTFOUND");
        }
        IssuedPermit permit = permitOptional.get();
        permit.setRevoked(true);
        permit.setRevokedAt(LocalDateTime.now());
        issuedPermitRepository.save(permit);
        permitRevokedEventFactory.create(permit.getIssuedFor(), permit.getPermitId());
    }

}

