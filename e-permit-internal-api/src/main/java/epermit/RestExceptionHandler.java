package epermit;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import lombok.Getter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleIllegalArgument(final Exception ex) {
        final int hataKodu = ThreadLocalRandom.current().nextInt(0, 99999);
        final String hataMesaji = "Beklenmeyen bir hata oluştu";
        MDC.put("hataKodu", Integer.toString(hataKodu));
        logger.error(hataMesaji, ex);
    
        return buildResponseEntity(new ApiHata(HttpStatus.BAD_REQUEST, hataMesaji, hataKodu));
    }

    private ResponseEntity<Object> buildResponseEntity(final ApiHata apiError) {
        return new ResponseEntity<>(apiError, apiError.getHttpKodu());
    }

    @Getter
    private class ApiHata {

        private HttpStatus httpKodu;
        private String hataMesaji;
        private int hataKodu;

        public ApiHata(HttpStatus httpKodu, String hataMesaji, int hataKodu) {
            this.httpKodu = httpKodu;
            this.hataMesaji = hataMesaji;
            this.hataKodu = hataKodu;
        }
    }
}
