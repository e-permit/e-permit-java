package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)

public class EventControllerTest {

   @Mock
   EventService eventService;

   @InjectMocks
   EventController controller;

   @Test
   void receiveEventOkTest() {
      PermitCreatedLedgerEvent e = new PermitCreatedLedgerEvent("TR", "UZ", "0");
      HttpHeaders headers = new HttpHeaders();
      headers.add("authorization", "1234");
      controller.permitCreated(headers, e);
   }

   @Test
   void receiveEventErrorTest() {
      PermitCreatedLedgerEvent e = new PermitCreatedLedgerEvent("TR", "UZ", "0");
      HttpHeaders headers = new HttpHeaders();
      headers.add("authorization", "1234");
      doThrow(new EpermitValidationException("message", ErrorCodes.EVENT_ALREADY_EXISTS))
            .when(eventService).handleReceivedEvent(any(), any());
      EpermitValidationException ex =
            Assertions.assertThrows(EpermitValidationException.class, () -> {
               controller.permitCreated(headers, e);
            });
      assertEquals(ErrorCodes.EVENT_ALREADY_EXISTS.name(), ex.getErrorCode());
   }
}


