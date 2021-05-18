package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TrustedAuthority {
    private String code;
  
    private List<PublicKey> keys = new ArrayList<>();
}
