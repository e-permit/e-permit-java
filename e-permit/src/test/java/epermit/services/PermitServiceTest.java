package epermit.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.events.permitused.PermitUsedEventFactory;
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
    void getByIdTest() {}
    @Test
    void getAllTest() {}
    @Test
    void usePermitTest() {}
    
}
