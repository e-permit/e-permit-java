package epermit.models.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;

@Data
public class QuotaDto {

    private String permitIssuer;

    private String permitIssuedFor;

    private int permitYear;

    private Integer permitType;

    private Long balance;

    private Long nextSerial;

    @Getter(lazy=true)
    private final Long issuedCount = nextSerial - 1;

    private Long revokedCount;

    private List<QuotaEvent> events = new ArrayList<>();
}
