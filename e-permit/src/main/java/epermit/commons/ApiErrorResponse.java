package epermit.commons;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ApiErrorResponse {
    private HttpStatus status;
    private String timestamp;
    private String errorId;
    private String errorMessage;

    public ApiErrorResponse(HttpStatus status, String errorMessage) {
        this.status = status;
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC).toString();
        this.errorId = UUID.randomUUID().toString();
        this.errorMessage = errorMessage;
    }

    public ApiErrorResponse(Exception ex) {
        this(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error!!, please contact system manager with error id");
    }

    public ApiErrorResponse(EpermitValidationException ex) {
        this(HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error" + "(" + ex.getErrorCode()
                + "), please contact system manager with error id");
    }
}
