package epermit.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitConfigKeyDto {
    private String kty;
    private String use;
    private String crv;
    private String kid;
    private String x;
    private String y;
}
