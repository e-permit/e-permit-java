package epermit.controllers;

import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.permitused.PermitUsedCommand;
import epermit.common.CommandResult;
import epermit.dtos.PermitDto;
import epermit.repositories.PermitRepository;

@RestController
@RequestMapping("/permits")
public class PermitController {

    private final PermitRepository repository;
    private final Pipeline pipeline;
    private final ModelMapper modelMapper;

    public PermitController(PermitRepository repository, Pipeline pipeline, ModelMapper modelMapper) {
        this.repository = repository;
        this.pipeline = pipeline;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<Page<PermitDto>> getAll(Pageable pageable) {
        Page<epermit.entities.Permit> entities = repository.findAll(pageable);
        Page<PermitDto> dtoPage = entities.map(x -> modelMapper.map(x, PermitDto.class));
        return new ResponseEntity<>(dtoPage, HttpStatus.OK);
    }

    @PatchMapping("{id}/used")
    public CommandResult setUsed(@PathVariable String id, @Valid PermitUsedCommand cmd) {
        cmd.setId(id);
        return cmd.execute(pipeline);
    }
}
