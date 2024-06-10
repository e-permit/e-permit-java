package epermit.models.dtos;

import lombok.Data;

@Data
public class QuotaEvent {
    private Long quantity;
    private Long timestamp;
}
