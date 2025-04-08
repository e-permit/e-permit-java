package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import epermit.commons.ApiErrorResponse;
import epermit.commons.GsonUtil;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.LedgerEventCreated;
import epermit.repositories.CreatedEventRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final LedgerEventUtil ledgerEventUtil;
    private final CreatedEventRepository createdEventRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public void handleSentEvent(String eventId) {
        CreatedEvent event = createdEventRepository.findByEventId(eventId).get();
        event.setSent(true);
        createdEventRepository.save(event);
    }

    @Transactional
    public void handleEventError(String eventId, String error) {
        CreatedEvent event = createdEventRepository.findByEventId(eventId).get();
        event.setError(error);
        createdEventRepository.save(event);
    }

    @SneakyThrows
    public List<LedgerEventCreated> getUnSentEvents() {
        List<LedgerEventCreated> list = new ArrayList<>();
        List<CreatedEvent> events = createdEventRepository.findAllBySentFalseOrderByCreatedAtAsc();
        for (CreatedEvent createdEvent : events) {
            list.add(ledgerEventUtil.createAppEvent(createdEvent));
        }
        return list;
    }

    @Transactional
    public <T extends LedgerEventBase> void handleReceivedEvent(HttpHeaders headers, T e) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Event jws. {}", authorization);
        String proof = ledgerEventUtil.verifyProof(e, authorization);
        ledgerEventUtil.handleEvent(e, proof);
    }

    @SneakyThrows
    public void sendEvent(LedgerEventCreated event) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + event.getProof());
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(event.getContent(), headers);
            restTemplate.postForEntity(event.getUrl(), request, Object.class);
            handleSentEvent(event.getEventId());
        }catch(HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                Object body = e.getResponseBodyAs(Object.class);
                ApiErrorResponse error = (ApiErrorResponse) body;
                if (error != null) {
                    var errorCode = error.getDetails().get("errorCode");
                    if (errorCode.equals("EVENT_ALREADY_EXISTS")) {
                        handleSentEvent(event.getEventId());
                    } else if (errorCode.equals("PREVIOUS_EVENT_NOTFOUND")) {
                        handleEventError(event.getEventId(), "Previous event not found");
                    } else {
                        String err = GsonUtil.getGson().toJson(body);
                        log.error(err);
                        handleEventError(event.getEventId(), err);
                    }
                }
            }else {
                log.error(e.getMessage(), e);
            }
        }
    }

    static Specification<LedgerEvent> filterEvents(Long id, String producer, String consumer) {
        Specification<LedgerEvent> spec = (event, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(event.get("producer"), producer));
            predicates.add(cb.equal(event.get("consumer"), consumer));
            predicates.add(cb.greaterThan(event.get("id"), id));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}