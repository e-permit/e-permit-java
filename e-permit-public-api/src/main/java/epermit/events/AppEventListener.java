package epermit.events;


import java.util.Optional;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.common.PermitProperties;
import epermit.entities.Authority;
import epermit.repositories.AuthorityRepository;
import epermit.services.KeyService;
import lombok.extern.slf4j.Slf4j;

@EnableAsync(proxyTargetClass = true)
@Component
@Slf4j
public class AppEventListener {
    private final RestTemplate restTemplate;

    private final KeyService keyService;

    private final AuthorityRepository authorityRepository;

    public AppEventListener(RestTemplate restTemplate, KeyService keyService, AuthorityRepository authorityRepository) {
        this.restTemplate = restTemplate;
        this.keyService = keyService;
        this.authorityRepository = authorityRepository;
    }

    @Async
    @EventListener
    public void onAppEvent(AppEvent event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JwsValidationResult r = keyService.validateJws(event.getJws());
        if(r.isValid()){
            Authority authority = authorityRepository.findByCode(r.getIssuer()).get();
            String apiUri = authority.getApiUri();
            // lock
            // get all
            // handle for each
            // unlock
        }
    }
}
