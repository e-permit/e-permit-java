package epermit.services;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.entities.Permit;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.models.PermitActivityType;
import epermit.models.PermitDto;
import epermit.repositories.PermitRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermitService {
    private final ModelMapper modelMapper;
    private final PermitRepository permitRepository;
    private final PermitUsedEventFactory permitUsedEventFactory;

    public PermitDto getById(Long id) {
        PermitDto dto = modelMapper.map(permitRepository.findById(id).get(), PermitDto.class);
        return dto;
    }

    public Page<PermitDto> getAll(Pageable pageable) {
        Page<Permit> entities = permitRepository.findAll(pageable);
        return entities.map(x -> modelMapper.map(x, PermitDto.class));
    }

    @Transactional
    public void usePermit(Long id, PermitActivityType activityType) {
        Optional<Permit> permitOptional = permitRepository.findById(id);
        if (!permitOptional.isPresent()) {

        }
        Permit permit = permitOptional.get();

        permitRepository.save(permit);
        permitUsedEventFactory.create(permit.getIssuer(), permit.getPermitId(), activityType);
    }

}
