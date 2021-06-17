package epermit.ledger.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.Instant;
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
import epermit.ledger.repositories.LedgerPermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitServiceTest {
    @Spy
    private ModelMapper modelMapper;
    @Mock
    private LedgerPermitRepository permitRepository;


    @InjectMocks
    PermitService permitService;

    /*@Test
    void getByIdTest() {
        Long id = Long.valueOf(1);
        Permit permit = new Permit();
        permit.setPermitId("permitId");
        when(permitRepository.findById(id)).thenReturn(Optional.of(permit));
        PermitDto dto = permitService.getById(id);
        assertNotNull(dto);
    }

    @Test
    void getAllTest() {
        Permit permit = new Permit();
        permit.setPermitId("permitId");

        PermitListInput input = new PermitListInput();
        input.setPage(1);
        Page<Permit> pagedList = new PageImpl<>(List.of(permit));

        when(permitRepository.findAll(ArgumentMatchers.<Specification<Permit>>any(),
                ArgumentMatchers.<Pageable>any())).thenReturn(pagedList);
        Page<PermitDto> result = permitService.getAll(input);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void usePermitNotFoundTest() {
        assertThrows(ResponseStatusException.class, () -> {
            permitService.usePermit("", new PermitUsedInput());
        });
    }

    @Test
    void usePermitTest() {
        PermitUsedInput input = new PermitUsedInput();
        input.setActivityType(PermitActivityType.ENTERANCE);
        Permit permit = new Permit();
        permit.setPermitId("TR-UZ-2021-1-1");
        PermitActivity activity = new PermitActivity();
        activity.setActivityType(PermitActivityType.ENTERANCE);
        activity.setActivityTimestamp(Instant.now().getEpochSecond());
        permit.addActivity(activity);
        when(permitRepository.findOneByPermitId("TR-UZ-2021-1-1")).thenReturn(Optional.of(permit));
        permitService.usePermit("TR-UZ-2021-1-1", input);
        verify(permitRepository, times(1)).save(permit);
        verify(permitUsedEventFactory, times(1)).create(activity);
    }*/

}
