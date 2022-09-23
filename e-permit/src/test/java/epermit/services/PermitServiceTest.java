package epermit.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import epermit.entities.SerialNumber;
import epermit.entities.LedgerPermit;
import epermit.ledgerevents.LedgerEventType;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.dtos.PermitListParams;
import epermit.models.enums.PermitActivityType;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.SerialNumberRepository;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.utils.PermitUtil;

@ExtendWith(MockitoExtension.class)
public class PermitServiceTest {
    @Spy
    private ModelMapper modelMapper;

    @Mock
    private LedgerPermitRepository permitRepository;

    @Mock
    PermitUtil permitUtil;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    SerialNumberRepository serialNumberRepository;

    @Mock
    EPermitProperties properties;

    @Mock
    LedgerEventUtil ledgerEventUtil;

    @Captor
    ArgumentCaptor<PermitCreatedLedgerEvent> createdCaptor;


    @Captor
    ArgumentCaptor<PermitUsedLedgerEvent> usedCaptor;

    @Captor
    ArgumentCaptor<PermitRevokedLedgerEvent> revokedCaptor;


    @InjectMocks
    PermitService permitService;

    @Test
    void getByIdTest() {
        UUID id = UUID.randomUUID();
        LedgerPermit permit = new LedgerPermit();
        permit.setPermitId("permitId");
        when(permitRepository.findById(id)).thenReturn(Optional.of(permit));
        PermitDto dto = permitService.getById(id);
        assertNotNull(dto);
    }

    @Test
    void getAllTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setPermitId("permitId");

        PermitListParams input = new PermitListParams();
        input.setPage(1);
        Page<LedgerPermit> pagedList = new PageImpl<>(List.of(permit));

        when(permitRepository.findAll(ArgumentMatchers.<Specification<LedgerPermit>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(pagedList);
        Page<PermitListItem> result = permitService.getAll(input);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void createPermitTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setIssuedFor("UZ");
        input.setCompanyId("companyId");
        input.setCompanyName("companyName");
        input.setPlateNumber("plateNumber");
        SerialNumber serialNumber = new SerialNumber();
        serialNumber.setSerialNumber(1);

        when(serialNumberRepository.findAll(ArgumentMatchers.<Specification<SerialNumber>>any(),
                ArgumentMatchers.<Pageable>any()))
                        .thenReturn(new PageImpl<>(List.of(serialNumber)));
        when(properties.getIssuerCode()).thenReturn("TR");
        when(ledgerEventUtil.getPreviousEventId("UZ")).thenReturn("123");
        when(permitUtil.generateQrCode(any())).thenReturn("QR");
        when(permitUtil.getPermitId(any())).thenReturn("TR-UZ-2021-1-1");
        CreatePermitResult result = permitService.createPermit(input);
        assertEquals("TR-UZ-2021-1-1", result.getPermitId());
        assertEquals("QR", result.getQrCode());
        verify(ledgerEventUtil, times(1)).persistAndPublishEvent(createdCaptor.capture());
        PermitCreatedLedgerEvent event = createdCaptor.getValue();
        assertEquals("TR", event.getEventProducer());
        assertEquals("UZ", event.getEventConsumer());
        assertEquals(LedgerEventType.PERMIT_CREATED, event.getEventType());
        assertEquals("TR", event.getPermitIssuer());
        assertEquals("UZ", event.getPermitIssuedFor());
        assertEquals(PermitType.BILITERAL, event.getPermitType());
        assertEquals(2021, event.getPermitYear());
        assertEquals("123", event.getPreviousEventId());
    }

    @Test
    void permitUsedTest() {
        PermitUsedInput input = new PermitUsedInput();
        input.setActivityDetails("Details");
        input.setActivityTimestamp(Long.valueOf(12344));
        input.setActivityType(PermitActivityType.ENTRANCE);
        when(properties.getIssuerCode()).thenReturn("TR");
        when(ledgerEventUtil.getPreviousEventId("UZ")).thenReturn("123");
        LedgerPermit permit = new LedgerPermit();
        permit.setPermitId("permitId");
        permit.setIssuer("UZ");
        when(permitRepository.findOneByPermitId("UZ-TR-2021-1-1")).thenReturn(Optional.of(permit));
        permitService.permitUsed("UZ-TR-2021-1-1", input);
        verify(ledgerEventUtil, times(1)).persistAndPublishEvent(usedCaptor.capture());
        PermitUsedLedgerEvent event = usedCaptor.getValue();
        assertEquals("TR", event.getEventProducer());
        assertEquals("UZ", event.getEventConsumer());
        assertEquals(LedgerEventType.PERMIT_USED, event.getEventType());
        assertEquals("Details", event.getActivityDetails());
        assertEquals(Long.valueOf(12344), event.getActivityTimestamp());
        assertEquals(PermitActivityType.ENTRANCE, event.getActivityType());
        assertEquals("UZ-TR-2021-1-1", event.getPermitId());
        assertEquals("123", event.getPreviousEventId());
    }

    @Test
    void permitRevokedTest() {
        when(properties.getIssuerCode()).thenReturn("TR");
        when(ledgerEventUtil.getPreviousEventId("UZ")).thenReturn("123");
        LedgerPermit permit = new LedgerPermit();
        permit.setPermitId("TR-UZ-2021-1-1");
        permit.setIssuer("TR");
        permit.setIssuedFor("UZ");
        SerialNumber serialNumber = new SerialNumber();
        when(serialNumberRepository.findOne(ArgumentMatchers.<Specification<SerialNumber>>any()))
                .thenReturn(Optional.of(serialNumber));
        when(permitRepository.findOneByPermitId("TR-UZ-2021-1-1")).thenReturn(Optional.of(permit));
        permitService.revokePermit("TR-UZ-2021-1-1");
        verify(ledgerEventUtil, times(1)).persistAndPublishEvent(revokedCaptor.capture());
        PermitRevokedLedgerEvent event = revokedCaptor.getValue();
        assertEquals("TR", event.getEventProducer());
        assertEquals("UZ", event.getEventConsumer());
        assertEquals(LedgerEventType.PERMIT_REVOKED, event.getEventType());
        assertEquals("TR-UZ-2021-1-1", event.getPermitId());
        assertEquals("123", event.getPreviousEventId());
    }

}
