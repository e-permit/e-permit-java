package epermit.controllers;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.dtos.ConfigDto;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/epermit-configuration")
public class ConfigController {
    //private final EPermitProperties props;
    //private final KeyRepository keyRepository;
    //private final AuthorityRepository authorityRepository;

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
        List<epermit.common.Authority> authorityDtoList = new ArrayList<>();
        List<Authority> authorities = authorityRepository.findAll();
        authorities.forEach(authority -> {
            epermit.common.Authority authorityDto = new epermit.common.Authority();
            authorityDto.setCode(authority.getCode());
            authorityDto.setVerifyUri(authority.getVerifyUri());
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
