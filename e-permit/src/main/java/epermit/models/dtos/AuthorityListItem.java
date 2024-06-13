package epermit.models.dtos;

import lombok.Data;

@Data
public class AuthorityListItem {
  private String code;

  private String name;

  private String publicApiUri;
}

