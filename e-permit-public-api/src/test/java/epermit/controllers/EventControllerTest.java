package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import epermit.events.ReceivedAppEvent;
import epermit.repositories.CreatedEventRepository;
import epermit.services.CreatedEventService;

@ExtendWith(MockitoExtension.class)

public class EventControllerTest {
    @Mock 
    ApplicationEventPublisher applicationEventPublisher;

    @Mock
    CreatedEventService createdEventService;

    @InjectMocks
    EventController controller;

    @Test
    void receiveEventTest(){ 
       Boolean r =  controller.receiveEvent(Map.of("jws", "jws"));
       assertTrue(r);
       ReceivedAppEvent appEvent = new ReceivedAppEvent();
       appEvent.setJws("jws");
       verify(applicationEventPublisher, times(1)).publishEvent(appEvent);
    }

    @Test
    void getEventsTest(){
       when(createdEventService.getEvents("jws")).thenReturn(List.of("abc"));
       List<String> r = controller.getEvents("jws");
       assertEquals(1, r.size());
    }

}




















/*
     * @Test void receiveEventShouldReturnTrue() { EventPublisher publisher = new EventPublisher();
     * EventController controller = new EventController(publisher, eventRepository); Map<String,
     * String> input = new HashMap<>(); input.put("jws", "jws"); Boolean r =
     * controller.receiveEvent(input); AppEvent event = (AppEvent)publisher.getEvent();
     * assertEquals("jws", event.getJws()); assertTrue(r); }
     * 
     * @Test void getEventsTest(){ Map<String, Object> claims = new HashMap<>();
     * claims.put("issuer", "TR"); claims.put("event_id", "1"); String jws =
     * JsonUtil.createDummyJws(claims); CreatedEvent e = new CreatedEvent(); e.setId(15);
     * List<CreatedEvent> list = new ArrayList<>(); list.add(e);
     * when(eventRepository.findOneByEventIdAndIssuedFor("1", "TR")).thenReturn(Optional.of(e));
     * when(eventRepository.findByIdGreaterThanOrderByIdAsc(Long.valueOf(15))).thenReturn(list);
     * EventController controller = new EventController(null, eventRepository); List<String> r =
     * controller.getEvents(jws); assertEquals(1, r.size()); }
     */

    /*
     * void getTest() { when(props.getKeyPassword()).thenReturn("123456"); Key key =
     * keyUtil.create("1"); List<Key> keys = new ArrayList<>(); keys.add(key);
     * when(props.getIssuerCode()).thenReturn("TR");
     * when(props.getIssuerVerifyUri()).thenReturn("http://localhost");
     * when(keyRepository.findAll()).thenReturn(keys); AuthorityConfig dto = controller.getConfig();
     * assertEquals("http://localhost", dto.getVerifyUri()); assertEquals("TR", dto.getCode()); }
     */
    // @Test
    /*
     * public void getTest() throws Exception {
     * when(eventService.handle(anyString())).thenReturn(EventHandleResult.success()); AppEvent
     * input = new AppEvent(); input.setJws("jws"); String json = new
     * ObjectMapper().writeValueAsString(input); System.out.println(json); MvcResult mvcResult =
     * mockMvc .perform(MockMvcRequestBuilders.post("/events")
     * .contentType(MediaType.APPLICATION_JSON) .content(json))
     * .andDo(print()).andExpect(status().isOk()).andReturn();
     * System.out.println(mvcResult.getResponse().getStatus()); EventHandleResult r =
     * parseResponse(mvcResult, EventHandleResult.class); assertEquals(r.isSucceed(), true); }
     * 
     * private static final ObjectMapper MAPPER = new
     * ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
     * .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) .registerModule(new
     * JavaTimeModule());
     * 
     * public static <T> T parseResponse(MvcResult result, Class<T> responseClass) { try { String
     * contentAsString = result.getResponse().getContentAsString(); return
     * MAPPER.readValue(contentAsString, responseClass); } catch (IOException e) { throw new
     * RuntimeException(e); } }
     */