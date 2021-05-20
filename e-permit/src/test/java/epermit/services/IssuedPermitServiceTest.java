package epermit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import epermit.entities.IssuedPermit;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.models.EPermitProperties;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.repositories.IssuedPermitRepository;
import epermit.utils.PermitUtil;

@ExtendWith(MockitoExtension.class)
public class IssuedPermitServiceTest {

    @Spy
    ModelMapper modelMapper;

    @Mock
    PermitUtil permitUtil;

    @Mock
    EPermitProperties properties;

    @Mock
    IssuedPermitRepository issuedPermitRepository;

    @Mock
    PermitCreatedEventFactory permitCreatedEventFactory;

    @Mock
    PermitRevokedEventFactory permitRevokedEventFactory;

    @InjectMocks
    IssuedPermitService issuedPermitService;

    @Test
    void getByIdTest() {
        Long id = Long.valueOf(1);
        IssuedPermit permit = new IssuedPermit();
        permit.setPermitId("permitId");
        when(issuedPermitRepository.findById(id)).thenReturn(Optional.of(permit));
        IssuedPermitDto dto = issuedPermitService.getById(id);
        assertNotNull(dto);
    }
    @Test
    void getAllTest() {
        IssuedPermit permit = new IssuedPermit();
        permit.setPermitId("permitId");
        
        Pageable pageable = PageRequest.of(2, 20);
        Page<IssuedPermit> pagedList = new PageImpl<>(List.of(permit));
        
        when(issuedPermitRepository.findAll(pageable)).thenReturn(pagedList);
        Page<IssuedPermitDto> result = issuedPermitService.getAll("UA", pageable);
        assertEquals(1, result.getContent().size());
    }
    @Test
    void createPermitTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyName("companyName");
        input.setIssuedFor("UZ");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("plate");
        when(permitUtil.generateSerialNumber("UZ", 2021, PermitType.BILITERAL)).thenReturn(5);
        when(permitUtil.getPermitId("TR", input.getIssuedFor(), input.getPermitType(), input.getPermitYear(), 5)).thenReturn("TR-UA-2021-1-5");
        when(properties.getIssuerCode()).thenReturn("TR");
        when(permitUtil.generateQrCode(any())).thenReturn("QRCODE");
        String permitId = issuedPermitService.createPermit(input);
        assertEquals("TR-UA-2021-1-5", permitId);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName("companyName");
        permit.setIssuedFor("UZ");
        permit.setPermitId("TR-UA-2021-1-5");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("plate");
        permit.setIssuedAt(OffsetDateTime.now().format(dtf));
        permit.setExpireAt("30/01/2022");
        permit.setQrCode("QRCODE");
        permit.setSerialNumber(5);
        verify(issuedPermitRepository).save(permit);
    }
    @Test
    void revokePermitTest() {}
}

/**
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
        permit.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));
        issuedPermitRepository.save(permit);
        permitRevokedEventFactory.create(permit.getIssuedFor(), permit.getPermitId());
    }

 */