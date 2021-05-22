package epermit.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Permit;
import epermit.entities.PermitActivity;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.models.dtos.PermitDto;
import epermit.models.enums.PermitActivityType;
import epermit.models.inputs.PermitUsedInput;
import epermit.repositories.PermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitServiceTest {
    @Spy
    private ModelMapper modelMapper;
    @Mock
    private PermitRepository permitRepository;
    @Mock
    private PermitUsedEventFactory permitUsedEventFactory;

    @InjectMocks
    PermitService permitService;

    @Test
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

        Pageable pageable = PageRequest.of(2, 20);
        Page<Permit> pagedList = new PageImpl<>(List.of(permit));

        when(permitRepository.findAll(pageable)).thenReturn(pagedList);
        Page<PermitDto> result = permitService.getAll(pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void usePermitNotFoundTest() {
        assertThrows(ResponseStatusException.class, () -> {
            permitService.usePermit(new PermitUsedInput());
        });
    }
    @Test
    void usePermitTest() {
        PermitUsedInput input =  new PermitUsedInput();
        input.setActivityType(PermitActivityType.ENTERANCE);
        input.setPermitId("TR-UZ-2021-1-1");
        Permit permit = new Permit();
        permit.setPermitId("TR-UZ-2021-1-1");
        PermitActivity activity = new PermitActivity();
        activity.setActivityType(PermitActivityType.ENTERANCE);
        permit.addActivity(activity);
        when( permitRepository.findOneByPermitId("TR-UZ-2021-1-1")).thenReturn(Optional.of(permit));
        permitService.usePermit(input);
        verify(permitRepository, times(1)).save(permit);
        verify(permitUsedEventFactory, times(1)).create(permit, PermitActivityType.ENTERANCE);
    }
    
}
