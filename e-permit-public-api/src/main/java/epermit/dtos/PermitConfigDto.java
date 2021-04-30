package epermit.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PermitConfigDto {

    private String code;

    private String apiUri;

    private String verifyUri;

    private List<PermitConfigKeyDto> keys = new ArrayList<>();

    private List<PermitConfigAuthorityDto> authorities = new ArrayList<>();
}

