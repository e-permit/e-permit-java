package epermit.dtos;

import java.util.Date;
import lombok.Data;

@Data
public class AuthorityKeyDto {
  
    private int id;

    private String kid;

    private Boolean isActive;
    
    private Date createdAt;

    private Date disabledAt;

    private String content;
}
