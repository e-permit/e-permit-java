package epermit.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.common.JsonUtil;
import epermit.config.EPermitProperties;
import epermit.dtos.PermitConfigAuthorityDto;
import epermit.dtos.PermitConfigDto;
import epermit.dtos.PermitConfigKeyDto;
import epermit.entities.Authority;
import epermit.repositories.AuthorityRepository;
import epermit.services.KeyService;


@RestController
@RequestMapping("/epermit-configuration")
public class ConfigController {
    private final EPermitProperties props;
    private final KeyService keyService;
    private final AuthorityRepository authorityRepository;
    private final ModelMapper modelMapper;

    public ConfigController(EPermitProperties props, KeyService keyService,
            AuthorityRepository authorityRepository, ModelMapper modelMapper) {
        this.authorityRepository = authorityRepository;
        this.keyService = keyService;
        this.props = props;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public PermitConfigDto get() {
        PermitConfigDto dto = new PermitConfigDto();
        dto.setCode(props.getIssuer().getCode());
        dto.setApiUri(props.getIssuer().getApiUri());
        dto.setVerifyUri(props.getIssuer().getVerifyUri());
        String jwk = keyService.getKey().toPublicJWK().toJSONString();     
        Gson gson = JsonUtil.getGson();
        PermitConfigKeyDto keyDto = gson.fromJson(jwk, PermitConfigKeyDto.class);
        List<PermitConfigKeyDto> keyDtoList = new ArrayList<>();
        keyDtoList.add(keyDto);
        dto.setKeys(keyDtoList);
        List<Authority> authorities = authorityRepository.findAll();
        List<PermitConfigAuthorityDto> authorityDtoList =
                authorities.stream().map(x -> modelMapper.map(x, PermitConfigAuthorityDto.class))
                        .collect(Collectors.toList());
        dto.setAuthorities(authorityDtoList);
        return dto;
    }
}
