package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AuthorityConfig {
    private String code;

    private String name;
  
    private String verifyUri;
  
    private List<PublicJwk> keys = new ArrayList<>();
}
