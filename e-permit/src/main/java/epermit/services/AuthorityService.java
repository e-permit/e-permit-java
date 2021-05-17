package epermit.services;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.models.AuthorityDto;
import epermit.models.CreateAuthorityInput;
import epermit.models.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {
    private final AuthorityRepository authorityRepository;
    private final ModelMapper modelMapper;

    public List<AuthorityDto> getAll() {
        List<epermit.entities.Authority> all = authorityRepository.findAll();
        return all.stream().map(x -> modelMapper.map(x, AuthorityDto.class))
                .collect(Collectors.toList());
    }

    public AuthorityDto getByCode(String code) {
        Authority authority = authorityRepository.findOneByCode(code).get();
        return modelMapper.map(authority, AuthorityDto.class);
    }

    public void create(CreateAuthorityInput input) {
        log.info("Authority create command: " + input.getApiUri());

    }

    public void createQuota(CreateQuotaInput input) {

    }

    public void enableQuota(Long id) {

    }
}
