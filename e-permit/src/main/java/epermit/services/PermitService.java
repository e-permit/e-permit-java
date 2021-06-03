package epermit.services;

import java.time.Instant;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Permit;
import epermit.entities.PermitActivity;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.PermitUsedInput;
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
    public void usePermit(String permitId, PermitUsedInput input) {
        Optional<Permit> permitOptional = permitRepository.findOneByPermitId(permitId);
        if (!permitOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PERMIT_NOTFOUND");
        }
        Permit permit = permitOptional.get();
        PermitActivity activity = new PermitActivity();
        activity.setActivityType(input.getActivityType());
        activity.setActivityTimestamp(Instant.now().getEpochSecond());
        permit.addActivity(activity);
        permitRepository.save(permit);
        permitUsedEventFactory.create(activity);
    }

}
