package epermit.dtos;

import java.util.ArrayList;
import java.util.List;
import epermit.common.PublicKey;
import lombok.Data;

@Data
public class ConfigDto {

    private String code;

    private String verifyUri;

    private List<PublicKey> keys = new ArrayList<>();

    private List<AuthorityDto> authorities = new ArrayList<>();
}

