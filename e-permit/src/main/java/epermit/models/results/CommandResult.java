package epermit.models.results;


import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class CommandResult<T> {

    private boolean ok;

    private String errorCode;

    private String errorMessage;

    private OffsetDateTime timestamp;

    private T data;

    public CommandResult(T data) {
        this.timestamp = OffsetDateTime.now();
        this.ok = true;
        this.data = data;
    }

    public CommandResult(String errorCode, String errorMessage) {
        this.timestamp = OffsetDateTime.now();
        this.ok = false;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}


