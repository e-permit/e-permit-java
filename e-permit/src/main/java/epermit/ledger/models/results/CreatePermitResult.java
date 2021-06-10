package epermit.ledger.models.results;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Data;

@Data
public class CreatePermitResult {
    private boolean ok;

    private String errorCode;

    private LocalDateTime timestamp;

    private String permitId;

    
    public static CreatePermitResult success(String permitId){
        CreatePermitResult r = new CreatePermitResult();
        r.ok = true;
        r.permitId = permitId;
        r.timestamp = LocalDateTime.now(ZoneOffset.UTC);
        return r;
    }

    public static CreatePermitResult fail(String errorCode){
        CreatePermitResult r = new CreatePermitResult();
        r.ok = false;
        r.errorCode = errorCode;
        r.timestamp = LocalDateTime.now(ZoneOffset.UTC);
        return r;
    }
}
