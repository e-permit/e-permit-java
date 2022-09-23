package epermit.controllers;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.models.dtos.IndexDto;
import epermit.services.AuthorityService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class IndexController {
    private final AuthorityService service;

    @GetMapping()
    public IndexDto index() {
        IndexDto dto = new IndexDto();
        dto.setAuthorities(
                service.getAll().stream().map(x -> x.getCode()).collect(Collectors.toList()));
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails user = (UserDetails) principal;
            dto.setUsername(user.getUsername());
            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
            dto.setRoles(
                    authorities.stream().map(x -> x.getAuthority()).collect(Collectors.toList()));
        }
        return dto;
    }
}

