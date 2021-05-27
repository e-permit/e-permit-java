package epermit.models.dtos;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AuthorityDto {
  private int id;

  private String code;

  private String name;

  private String apiUri;

  private String verifyUri;

  private List<PublicJwk> keys = new ArrayList<>();

  private List<VerifierQuotaDto> verifierQuotas = new ArrayList<>();

  private List<IssuerQuotaDto> issuerQuotas = new ArrayList<>();
}

