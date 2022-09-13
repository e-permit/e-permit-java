package epermit;

import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import epermit.commons.ApiErrorResponse;
import epermit.commons.EpermitValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(annotations = RestController.class)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EpermitValidationException.class)
    public ApiErrorResponse handleEpermitValidation(final EpermitValidationException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(ex);
        return apiError;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleInternalError(final Exception ex) {
        String errorCode = Long.toString(ThreadLocalRandom.current().nextLong(0, 99999999999L));
        ApiErrorResponse apiError = new ApiErrorResponse(ex, errorCode);
        MDC.put("epermitError", "INTERNAL_SERVER_ERROR");
        MDC.put("epermitErrorCode", errorCode);
        log.error(ex.getMessage(), ex);
        MDC.remove("epermitError");
        MDC.remove("epermitErrorCode");
        return apiError;
    }
}
