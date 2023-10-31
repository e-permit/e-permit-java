package epermit.models.results;

import lombok.Data;

@Data
public class CreatePermitResult {
    private boolean ok;

    private String permitId;

    private String qrCode;
    
    public static CreatePermitResult success(String permitId){
        CreatePermitResult r = new CreatePermitResult();
        r.ok = true;
        r.permitId = permitId;
        return r;
    }
}
