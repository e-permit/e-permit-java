package epermit.models.dtos;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class AuthorityDto {
  private UUID id;

  private String code;

  private String name;

  private String apiUri;

  private List<QuotaDto> quotas = new ArrayList<>();
}

