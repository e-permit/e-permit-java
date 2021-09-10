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
import epermit.commons.EpermitValidationException;
import lombok.Getter;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(EpermitValidationException.class)
    public ResponseEntity<Object> handleEpermitValidation(final EpermitValidationException ex) {
        final int errorCode = ThreadLocalRandom.current().nextInt(0, 99999);
        final String errorMessage = "Internal Server Error";
        MDC.put("errorCode", Integer.toString(errorCode));
        logger.error(errorMessage, ex);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(final Exception ex) {
        final int errorCode = ThreadLocalRandom.current().nextInt(0, 99999);
        final String errorMessage = "Internal Server Error";
        MDC.put("errorCode", Integer.toString(errorCode));
        logger.error(errorMessage, ex);
    
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", errorMessage));
    }

    private ResponseEntity<Object> buildResponseEntity(final ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @Getter
    private class ApiError {

        private HttpStatus status;
        private String errorCode;
        private String errorMessage;

        public ApiError(HttpStatus status, String errorCode, String errorMessage) {
            this.status = status;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
}
