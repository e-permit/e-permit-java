package epermit.services;

import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.entities.Permit;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.models.dtos.PermitDto;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CommandResult;
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
    public CommandResult usePermit(PermitUsedInput input) {
        Optional<Permit> permitOptional = permitRepository.findOneByPermitId(input.getPermitId());
        if (!permitOptional.isPresent()) {
            return CommandResult.fail("PERMIT_NOTFOUND");
        }
        Permit permit = permitOptional.get();
        permitRepository.save(permit);
        permitUsedEventFactory.create(permit, input.getActivityType());
        return CommandResult.success();
    }

}
