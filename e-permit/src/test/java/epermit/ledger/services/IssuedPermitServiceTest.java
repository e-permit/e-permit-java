package epermit.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.IssuedPermit;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.models.EPermitProperties;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.IssuedPermitListInput;
import epermit.models.results.CreatePermitResult;
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

        Pageable pageable = PageRequest.of(1, 10);
        Page<IssuedPermit> pagedList = new PageImpl<>(List.of(permit));
        IssuedPermitListInput input = new IssuedPermitListInput();
        input.setPage(1);
        when(issuedPermitRepository.findAll(ArgumentMatchers.<Specification<IssuedPermit>>any(),
                ArgumentMatchers.eq(pageable))).thenReturn(pagedList);
        Page<IssuedPermitDto> result = issuedPermitService.getAll(input);
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
        when(permitUtil.generateSerialNumber("UZ", 2021, PermitType.BILITERAL))
                .thenReturn(Optional.of(5));
        when(permitUtil.getPermitId("TR", input.getIssuedFor(), input.getPermitType(),
                input.getPermitYear(), 5)).thenReturn("TR-UA-2021-1-5");
        when(properties.getIssuerCode()).thenReturn("TR");
        when(permitUtil.generateQrCode(any())).thenReturn("QRCODE");
        CreatePermitResult r = issuedPermitService.createPermit(input);
        assertTrue(r.isOk());
        assertEquals("TR-UA-2021-1-5", r.getPermitId());
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
    void createPermitInsufficientSerialNumberTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyName("companyName");
        input.setIssuedFor("UZ");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("plate");
        when(permitUtil.generateSerialNumber("UZ", 2021, PermitType.BILITERAL))
                .thenReturn(Optional.empty());
        CreatePermitResult result = issuedPermitService.createPermit(input);
        assertFalse(result.isOk());
        assertEquals("INSUFFICIENT_QUOTA", result.getErrorCode());
    }

    @Test
    void revokePermitTest() {
        Long id = Long.valueOf(1);
        IssuedPermit permit = new IssuedPermit();
        permit.setIssuedFor("issuedFor");
        permit.setPermitId("permitId");
        when(issuedPermitRepository.findById(id)).thenReturn(Optional.of(permit));
        issuedPermitService.revokePermit(id);
        assertTrue(permit.isRevoked());
        assertNotNull(permit.getRevokedAt());
        verify(issuedPermitRepository, times(1)).save(permit);
        verify(permitRevokedEventFactory, times(1)).create("issuedFor", "permitId");
    }

    @Test
    void revokePermitNotFoundTest() {
        Long id = Long.valueOf(1);
        when(issuedPermitRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            issuedPermitService.revokePermit(id);
        });
    }
}
