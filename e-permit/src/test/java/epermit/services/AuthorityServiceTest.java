package epermit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.entities.Authority;
import epermit.ledgerevents.LedgerEventType;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.AuthorityListItem;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerQuotaRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class AuthorityServiceTest {
   
    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    LedgerQuotaRepository ledgerQuotaRepository;

    @Mock
    LedgerEventUtil ledgerEventUtil;

    @Spy
    ModelMapper modelMapper;

    @Mock
    EPermitProperties properties;

    @InjectMocks
    AuthorityService authorityService;

    @Captor
    ArgumentCaptor<QuotaCreatedLedgerEvent> captor;

    @Test
    @SneakyThrows
    void getAllTest() {
        Authority authority = new Authority();
        authority.setCode("C");
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        List<AuthorityListItem> dtos = authorityService.getAll();
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeTest() {
        Authority authority = new Authority();
        authority.setCode("B");
        when(authorityRepository.findOneByCode("B")).thenReturn(Optional.of(authority));
        AuthorityDto dto = authorityService.getByCode("B");
        assertEquals("B", dto.getCode());
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setPublicApiUri("apiUri");
        input.setCode("B");
        input.setName("CountryB");
        when(authorityRepository.findOneByCode("B")).thenReturn(Optional.empty());
        authorityService.create(input, new AuthorityConfig());
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
        authority.setCode("B");
        authority.setName("CountryB");
       
        verify(authorityRepository, times(1)).save(authority);
    }

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        input.setQuantity(20L);
        input.setPermitType(1);
        input.setPermitYear(2021);
        when(properties.getIssuerCode()).thenReturn("B");
        when(ledgerEventUtil.getPreviousEventId("A")).thenReturn("123");
        when(authorityRepository.findOneByCode("A")).thenReturn(Optional.of(new Authority()));
        authorityService.createQuota("A", input);
        verify(ledgerEventUtil, times(1)).persistAndPublishEvent(captor.capture());
        QuotaCreatedLedgerEvent event = captor.getValue();
        assertEquals(20L, event.getQuantity());
        assertEquals("A", event.getEventConsumer());
        assertEquals("B", event.getEventProducer());
        assertEquals(LedgerEventType.QUOTA_CREATED, event.getEventType());
        assertEquals("A", event.getPermitIssuer());
        assertEquals("B", event.getPermitIssuedFor());
        assertEquals(1, event.getPermitType());
        assertEquals(2021, event.getPermitYear());
        assertEquals("123", event.getPreviousEventId());
    }
}
