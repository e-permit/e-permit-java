package epermit;

import java.util.List;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.ReceivedEventRepository;
import epermit.services.KeyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventService {
    private final ReceivedEventRepository repository;
    private final AuthorityRepository authorityRepository;
    private final KeyService keyService;
    private final RestTemplate restTemplate;

    public EventService(RestTemplate restTemplate, ReceivedEventRepository repository,
            AuthorityRepository authorityRepository, KeyService keyService) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.authorityRepository = authorityRepository;
        this.keyService = keyService;
    }

    @Transactional
    @SneakyThrows
    public void handle(String jws) {
        log.info("The message is recived. The message content is: " + jws);
        String issuer = JsonUtil.getClaim(jws, "issuer");
        String issuedFor = JsonUtil.getClaim(jws, "issued_for");
        String messageId = JsonUtil.getClaim(jws, "event_id");
        JwsValidationResult validationResult = keyService.validateJws(jws);
        if (validationResult.isValid()) {
            Gson gson = JsonUtil.getGson();
            JWSObject jwsObject = JWSObject.parse(jws);
            String payload = jwsObject.getPayload().toString();
            //Command<MessageHandleResult> m = gson.fromJson(payload, clazz);
        } 
        // messageRepository.save(convertMessageToEntity(jws, resultJws));
        // get issuer, eventId, previousEventId
        // validate signature
        // if exists
        // get last
    }

    private void fetch(String eventId) {
        String[] list = restTemplate.getForObject("url", String[].class);
        
    }
}
