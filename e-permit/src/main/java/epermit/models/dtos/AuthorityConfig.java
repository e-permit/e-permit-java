package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AuthorityConfig {
    private String code;
  
    private String verifyUri;
  
    private List<PublicKey> keys = new ArrayList<>();

    private List<TrustedAuthority> trustedAuthorities = new ArrayList<>();
}
