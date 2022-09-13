package epermit.commons;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import lombok.Data;
import lombok.Getter;

@Getter
public class ApiErrorResponse {
    private HttpStatus status;
    private String timestamp;
    private String errorMessage;
    private List<Violation> details;

    public ApiErrorResponse(HttpStatus status, String errorMessage) {
        this.status = status;
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC).toString();
        this.errorMessage = errorMessage;
        this.details = new ArrayList<>();
    }

    public ApiErrorResponse(Exception ex, String errorCode) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error. Error code: " + errorCode);
    }

    public ApiErrorResponse(EpermitValidationException ex) {
        this(HttpStatus.BAD_REQUEST, "Validation error" + "(" + ex.getErrorCode() + ")");
    }

    @Data
    public class Violation {
        private String field;
        private String message;
    }
}
