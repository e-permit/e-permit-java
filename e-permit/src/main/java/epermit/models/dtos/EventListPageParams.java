package epermit.models.dtos;

import lombok.Data;

@Data
public class EventListPageParams {

    private Integer eventType;

    private Boolean sent;

    private String createdAt;

    private Integer page = 0;
}
