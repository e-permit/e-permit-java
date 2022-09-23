package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class IndexDto {
    private String username;
    private List<String> roles = new ArrayList<>();
    private List<String> authorities = new ArrayList<>();
}
