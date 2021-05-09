package epermit.dtos;

import java.util.List;
import epermit.common.PublicKey;
import lombok.Data;

@Data
public class AuthorityDto {
    private String code;
    private String name;
    private String apiUri;
    private String verifyUri;
    private List<PublicKey> keys;
}
