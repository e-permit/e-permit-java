package epermit.commons;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;

@Getter
public class ApiErrorResponse {
    private HttpStatus status;
    private String timestamp;
    private String errorId;
    private String errorMessage;
    @JsonInclude(value = Include.NON_EMPTY, content = Include.ALWAYS)
    private Map<String, Object> details = new HashMap<>();

    public ApiErrorResponse(HttpStatus status, String errorMessage) {
        this.status = status;
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC).toString();
        this.errorId = UUID.randomUUID().toString();
        this.errorMessage = errorMessage;
    }

    public ApiErrorResponse(Exception ex) {
        this(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error!!, please contact system manager with error id");
    }

    public ApiErrorResponse(EpermitValidationException ex) {
        this(HttpStatus.UNPROCESSABLE_ENTITY, "Error, please contact system manager with error id");
        details.put("errorCode", ex.getErrorCode());
    }

    public ApiErrorResponse(MethodArgumentNotValidException ex){
        this(HttpStatus.BAD_REQUEST, "Following validation errors occured");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            details.put(fieldName, errorMessage);
        });
    }
}
