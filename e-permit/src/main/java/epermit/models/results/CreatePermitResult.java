package epermit.models.results;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.Data;

@Data
public class CreatePermitResult {
    private boolean ok;

    private String errorCode;

    private LocalDateTime timestamp;

    private String permitId;

    private String qrCode;
    
    public static CreatePermitResult success(String permitId, String qrCode){
        CreatePermitResult r = new CreatePermitResult();
        r.ok = true;
        r.permitId = permitId;
        r.timestamp = LocalDateTime.now(ZoneOffset.UTC);
        r.qrCode = qrCode;
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
