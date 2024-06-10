package epermit.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.web.client.RestTemplate;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.entities.SerialNumber;
import epermit.ledgerevents.LedgerEventType;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.permitrevoked.PermitRevokedLedgerEvent;
import epermit.ledgerevents.permitused.PermitUsedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.dtos.PermitListPageParams;
import epermit.models.enums.PermitActivityType;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.repositories.SerialNumberRepository;
import epermit.utils.PermitUtil;

@ExtendWith(MockitoExtension.class)
public class PermitServiceTest {
    @Spy
    private ModelMapper modelMapper;

    @Mock
    private LedgerPermitRepository permitRepository;

    @Mock
    private LedgerQuotaRepository quotaRepository;

    @Mock
    PermitUtil permitUtil;

    @Mock
    RestTemplate restTemplate;

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

        PermitListPageParams input = new PermitListPageParams();
        input.setPage(1);
        Page<LedgerPermit> pagedList = new PageImpl<>(List.of(permit));

        when(permitRepository.findAll(ArgumentMatchers.<Specification<LedgerPermit>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(pagedList);
        Page<PermitListItem> result = permitService.getPage(input);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void createPermitTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setPermitType(1);
        input.setPermitYear(2021);
        input.setIssuedFor("UZ");
        input.setCompanyId("companyId");
        input.setCompanyName("companyName");
        input.setPlateNumber("plateNumber");
         when(serialNumberRepository.findOneByParams(anyString(), anyString(), anyInt(), anyInt() ))
                .thenReturn(Optional.of(SerialNumber.builder().nextSerial(1L).build()));      
        when(properties.getIssuerCode()).thenReturn("TR");
        when(ledgerEventUtil.getPreviousEventId("UZ")).thenReturn("123");
        CreatePermitResult result = permitService.createPermit(input);
        assertEquals("TR-UZ-2021-1-1", result.getPermitId());
        verify(ledgerEventUtil, times(1)).persistAndPublishEvent(createdCaptor.capture());
        PermitCreatedLedgerEvent event = createdCaptor.getValue();
        assertEquals("TR", event.getEventProducer());
        assertEquals("UZ", event.getEventConsumer());
        assertEquals(LedgerEventType.PERMIT_CREATED, event.getEventType());
        assertEquals("TR", event.getPermitIssuer());
        assertEquals("UZ", event.getPermitIssuedFor());
        assertEquals(Integer.valueOf(1), event.getPermitType());
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
        permit.setIssuedFor("TR");
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
