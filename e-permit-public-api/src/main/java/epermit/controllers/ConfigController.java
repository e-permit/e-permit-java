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
import epermit.common.PermitProperties;
import epermit.common.PublicJwk;
import epermit.common.PublicKey;
import epermit.dtos.AuthorityDto;
import epermit.dtos.ConfigDto;
import epermit.entities.Authority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;


@RestController
@RequestMapping("/epermit-configuration")
public class ConfigController {
    private final PermitProperties props;
    private final KeyRepository keyRepository;
    private final AuthorityRepository authorityRepository;

    public ConfigController(PermitProperties props, KeyRepository keyRepository,
            AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
        this.keyRepository = keyRepository;
        this.props = props;
    }

    @GetMapping
    public ConfigDto getConfig() {
        ConfigDto dto = new ConfigDto();
        dto.setCode(props.getIssuerCode());
        dto.setVerifyUri(props.getIssuerVerifyUri());
        Gson gson = JsonUtil.getGson();
        List<PublicKey> keyDtoList = new ArrayList<>();
        keyRepository.findAll().forEach(key -> {
            PublicKey publicKey = new PublicKey();
            publicKey.setKeyId(key.getKeyId());
            publicKey.setValidFrom(key.getValidFrom());
            publicKey.setValidUntil(key.getValidUntil());
            publicKey.setJwk(gson.fromJson(key.getPublicJwk(), PublicJwk.class));
            keyDtoList.add(publicKey);
        });
        dto.setKeys(keyDtoList);
        List<AuthorityDto> authorityDtoList = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(authority -> {
            AuthorityDto authorityDto = new AuthorityDto();
            authorityDto.setApiUri(authority.getApiUri());
            authorityDto.setCode(authority.getCode());
            authorityDto.setVerifyUri(authority.getVerifyUri());
            authorityDto.setName(authority.getName());
            List<PublicKey> publicKeys = new ArrayList<>();
            authority.getKeys().forEach(k -> {
                PublicKey publicKey = new PublicKey();
                publicKey.setKeyId(k.getKeyId());
                publicKey.setValidFrom(k.getValidFrom());
                publicKey.setValidUntil(k.getValidUntil());
                publicKey.setJwk(gson.fromJson(k.getJwk(), PublicJwk.class));
                publicKeys.add(publicKey);
            });
            authorityDto.setKeys(publicKeys);
            authorityDtoList.add(authorityDto);
        });

        dto.setAuthorities(authorityDtoList);
        return dto;
    }
}
