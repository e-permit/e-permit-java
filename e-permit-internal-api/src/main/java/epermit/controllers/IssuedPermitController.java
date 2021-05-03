package epermit.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import an.awesome.pipelinr.Pipeline;
import epermit.commands.createpermit.CreatePermitCommand;
import epermit.commands.revokepermit.RevokePermitCommand;
import epermit.common.CommandResult;
import epermit.repositories.IssuedPermitRepository;
import epermit.dtos.IssuedPermitDto;

@RestController
@RequestMapping("/issued_permits")
public class IssuedPermitController {

    private final IssuedPermitRepository repository;
    private final ModelMapper modelMapper;
    private final Pipeline pipeline;

    public IssuedPermitController(IssuedPermitRepository repository, ModelMapper modelMapper, Pipeline pipeline) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.pipeline = pipeline;
    }

    @GetMapping()
    public ResponseEntity<Page<IssuedPermitDto>> getAll(Pageable pageable) {
        Page<epermit.entities.IssuedPermit> entities = repository.findAll(pageable);
        Page<IssuedPermitDto> dtoPage = entities.map(x -> modelMapper.map(x, IssuedPermitDto.class));
        return new ResponseEntity<>(dtoPage, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public IssuedPermitDto getById(Long id) {
        IssuedPermitDto dto = modelMapper.map(repository.findById(id).get(), IssuedPermitDto.class);
        return dto;
    }

    @GetMapping("find/{permitId}")
    public IssuedPermitDto getBySerialNumber(String permitId) {
        IssuedPermitDto dto = modelMapper.map(repository.findOneByPermitId(permitId).get(), IssuedPermitDto.class);
        return dto;
    }

    @PostMapping()
    public CommandResult post(@RequestBody CreatePermitCommand cmd) {
        return cmd.execute(pipeline);
    }

    @PatchMapping("{id}/revoke")
    public CommandResult revoke(String id) {
        RevokePermitCommand cmd = new RevokePermitCommand();
        cmd.setPermitId(id);
        return cmd.execute(pipeline);
    }
}
