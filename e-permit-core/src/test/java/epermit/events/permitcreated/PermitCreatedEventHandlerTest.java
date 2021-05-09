package epermit.events.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.JsonUtil;
import epermit.common.PermitType;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Permit;
import epermit.entities.VerifierQuota;
import epermit.events.EventHandleResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedEventHandlerTest {
    @Mock
    AuthorityRepository repository;

    @Mock
    PermitRepository permitRepository;

    @Test
    void handleShouldWork() {
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.empty());
        Authority authority = new Authority();
        VerifierQuota verifierQuota = new VerifierQuota();
        verifierQuota.setPermitType(PermitType.BILITERAL);
        verifierQuota.setPermitYear(2021);
        verifierQuota.setStartNumber(1);
        verifierQuota.setEndNumber(5);
        authority.addVerifierQuota(verifierQuota);
        when(repository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        PermitCreatedEventHandler handler =
                new PermitCreatedEventHandler(permitRepository, repository);
        PermitCreatedEvent event = PermitCreatedEvent.builder().ExpireAt("A").IssuedAt("A")
                .companyName("A").permitId("UA-TR-2021-1-1").permitType(PermitType.BILITERAL)
                .permitYear(2021).plateNumber("A").serialNumber(1).build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isSucceed());
    }
    
    @Test
    void handleShouldReturnInvalidPermitIdWhenInvalidPermitIdGiven() {
        //Permit permit = new Permit
        //when(permitRepository.findOneByPermitId("UA-TR-2020-1-1")).thenReturn(Optional.empty());
        PermitCreatedEventHandler handler =
                new PermitCreatedEventHandler(permitRepository, repository);
        PermitCreatedEvent event = PermitCreatedEvent.builder().ExpireAt("A").IssuedAt("A")
                .companyName("A").permitId("UA-TR-2020-1-1").permitType(PermitType.BILITERAL)
                .permitYear(2021).plateNumber("A").serialNumber(1).build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");//"INVALID_PERMITID"
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("INVALID_PERMITID", r.getErrorCode());
    }

    @Test
    void handleShouldReturnPermitExistWhenPermitExist() {
        Permit permit = new Permit();
        permit.setId(Long.valueOf(1));
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.of(permit));
        PermitCreatedEventHandler handler =
                new PermitCreatedEventHandler(permitRepository, repository);
        PermitCreatedEvent event = PermitCreatedEvent.builder().ExpireAt("A").IssuedAt("A")
                .companyName("A").permitId("UA-TR-2021-1-1").permitType(PermitType.BILITERAL)
                .permitYear(2021).plateNumber("A").serialNumber(1).build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");//"INVALID_PERMITID"
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("PERMIT_EXIST", r.getErrorCode());
    }

    @Test
    void handleShouldReturnInSufficientQuotaWhen() {
        when(permitRepository.findOneByPermitId("UA-TR-2021-1-1")).thenReturn(Optional.empty());
        Authority authority = new Authority();
        VerifierQuota verifierQuota = new VerifierQuota();
        verifierQuota.setPermitType(PermitType.BILITERAL);
        verifierQuota.setPermitYear(2020);
        verifierQuota.setStartNumber(1);
        verifierQuota.setEndNumber(5);
        authority.addVerifierQuota(verifierQuota);
        when(repository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        PermitCreatedEventHandler handler =
                new PermitCreatedEventHandler(permitRepository, repository);
        PermitCreatedEvent event = PermitCreatedEvent.builder().ExpireAt("A").IssuedAt("A")
                .companyName("A").permitId("UA-TR-2021-1-1").permitType(PermitType.BILITERAL)
                .permitYear(2021).plateNumber("A").serialNumber(1).build();
        event.setIssuer("UA");
        event.setIssuedFor("TR");//"INVALID_PERMITID"
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isSucceed());
        assertEquals("QUOTA_DOESNT_MATCH", r.getErrorCode());
    }

    
}
