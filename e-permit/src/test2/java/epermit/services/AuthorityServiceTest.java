package epermit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.ledgerevents.LedgerEventType;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.enums.PermitType;
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
        authority.setCode("UA");
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        List<AuthorityDto> dtos = authorityService.getAll();
        assertEquals(1, dtos.size());
    }

    @Test
    void getByCodeTest() {
        Authority authority = new Authority();
        authority.setCode("UZ");
        when(authorityRepository.findOneByCode("UZ")).thenReturn(authority);
        AuthorityDto dto = authorityService.getByCode("UZ");
        assertEquals("UZ", dto.getCode());
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("apiUri");
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("UZ");
        config.setName("Uzbekistan");
        PublicJwk publicJwk = GsonUtil.getGson().fromJson(jwk, PublicJwk.class);
        config.setKeys(List.of(publicJwk));
        when(authorityRepository.findOneByCode("UZ")).thenReturn(null);
        authorityService.create(input, config);
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        authority.setApiUri("apiUri");
        LedgerPublicKey authorityKey = new LedgerPublicKey();
        authorityKey.setKeyId("1");
        authorityKey.setJwk(jwk);
       
        verify(authorityRepository, times(1)).save(authority);
    }

    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        input.setAuthorityCode("TR");
        input.setEndNumber(20);
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setStartNumber(1);
        when(properties.getIssuerCode()).thenReturn("UZ");
        when(ledgerEventUtil.getPreviousEventId("TR")).thenReturn("123");
        when(authorityRepository.findOneByCode("TR")).thenReturn(new Authority());
        authorityService.createQuota(input);
        verify(ledgerEventUtil, times(1)).persistAndPublishEvent(captor.capture());
        QuotaCreatedLedgerEvent event = captor.getValue();
        assertEquals(1, event.getStartNumber());
        assertEquals(20, event.getEndNumber());
        assertEquals("TR", event.getEventConsumer());
        assertEquals("UZ", event.getEventProducer());
        assertEquals(LedgerEventType.QUOTA_CREATED, event.getEventType());
        assertEquals("TR", event.getPermitIssuer());
        assertEquals("UZ", event.getPermitIssuedFor());
        assertEquals(PermitType.BILITERAL, event.getPermitType());
        assertEquals(2021, event.getPermitYear());
        assertEquals("123", event.getPreviousEventId());
    }
}
