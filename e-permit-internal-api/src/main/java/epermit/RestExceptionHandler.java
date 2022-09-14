package epermit;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import epermit.commons.ApiErrorResponse;
import epermit.commons.EpermitValidationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler  {

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleDeniedError(final AccessDeniedException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusError(final ResponseStatusException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(EpermitValidationException.class)
    public ApiErrorResponse handleEpermitValidation(final EpermitValidationException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(ex);
        addLog(apiError, ex);
        return apiError;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleInternalError(final Exception ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(ex);
        addLog(apiError, ex);
        return apiError;
    }

    private void addLog(ApiErrorResponse apiError, Exception ex) {
        MDC.put("epermitErrorId", apiError.getErrorId());
        log.error(ex.getMessage(), ex);
        MDC.remove("epermitErrorId");
    }
}
