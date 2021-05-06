package epermit.units;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import epermit.controllers.EventController;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.events.AppEvent;
import epermit.repositories.AuthorityRepository;
import epermit.services.EventService;

@WebMvcTest(EventController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Map<String, EventHandler> eventHandlers;

    @MockBean
    private EventService eventService;

    @MockBean
    private AuthorityRepository repository;

    @Test
    public void getTest() throws Exception {
        when(eventService.handle(anyString())).thenReturn(EventHandleResult.success());
        AppEvent input = new AppEvent();
        input.setJws("jws");
        String json = new ObjectMapper().writeValueAsString(input);
        System.out.println(json);
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        System.out.println(mvcResult.getResponse().getStatus());
        EventHandleResult r = parseResponse(mvcResult, EventHandleResult.class);
        assertEquals(r.isSucceed(), true);
    }

    private static final ObjectMapper MAPPER =
            new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule());

    public static <T> T parseResponse(MvcResult result, Class<T> responseClass) {
        try {
            String contentAsString = result.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

