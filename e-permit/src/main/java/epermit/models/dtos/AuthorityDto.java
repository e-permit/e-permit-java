package epermit.models.dtos;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AuthorityDto {
  private String code;

  private String name;

  private String publicApiUri;

  private List<QuotaDto> quotas = new ArrayList<>();

  private List<KeyDto> keys = new ArrayList<>();
}

