package epermit.events;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import epermit.entities.CreatedEvent;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppEventService {
    private final RestTemplate restTemplate;
    private final CreatedEventRepository createdEventRepository;
    private final AuthorityRepository authorityRepository;

    public AppEventService(RestTemplate restTemplate, CreatedEventRepository createdEventRepository,
            AuthorityRepository authorityRepository) {
        this.restTemplate = restTemplate;
        this.authorityRepository = authorityRepository;
        this.createdEventRepository = createdEventRepository;
    }

    public Boolean send(String uri, String jws) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(jws, headers);
        restTemplate.postForEntity(uri, request, Boolean.class);
        return true;
    }

    @Transactional
    public void handle() {

    }

    @Transactional
    public void watch(){
        List<CreatedEvent> list = createdEventRepository.findAll();
        
    }
}
