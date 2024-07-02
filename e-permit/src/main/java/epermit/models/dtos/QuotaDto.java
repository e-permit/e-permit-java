package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class QuotaDto {

    private String permitIssuer;

    private String permitIssuedFor;

    private int permitYear;

    private Integer permitType;

    private Long balance;

    private Long issuedCount;

    private Long revokedCount;

    private List<QuotaEvent> events = new ArrayList<>();
}
