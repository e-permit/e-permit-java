package epermit.ledgerevents.permitcreated;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import epermit.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCreatedLedgerEvent extends LedgerEventBase {
    public PermitCreatedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.PERMIT_CREATED);
    }

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}-[A-Z]{2}-\\d{4}-(1|2|3)-[0-9]+$")
    private String permitId;

    @NotNull
    private PermitType permitType;

    @NotNull
    @Size(min = 2, max = 2)
    private String permitIssuer;

    @NotNull
    @Size(min = 2, max = 2)
    private String permitIssuedFor;

    @NotNull
    @Min(2021)
    private int permitYear;

    @NotNull
    private int serialNumber;

    @NotNull
    @Pattern(regexp = "^(0?[1-9]|[12][0-9]|3[01])[/](0?[1-9]|1[012])[/]\\d{4}$")
    private String issuedAt;

    @NotNull
    @Pattern(regexp = "^(0?[1-9]|[12][0-9]|3[01])[/](0?[1-9]|1[012])[/]\\d{4}$")
    private String expireAt;

    @NotNull
    private String companyName;

    @NotNull
    private String companyId;

    @NotNull
    private String plateNumber;

    private Map<String, Object> otherClaims;

    @AssertTrue(message = "Invalid issued_at")
    private boolean isValidIssuedAt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate iat = LocalDate.parse(this.issuedAt, dtf);
        return iat.isBefore(LocalDate.now(ZoneOffset.UTC));
    }

    @AssertTrue(message = "Invalid expire_at")
    private boolean isValidExpireAt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate iat = LocalDate.parse(this.issuedAt, dtf);
        LocalDate exp = LocalDate.parse(this.expireAt, dtf);
        return iat.isBefore(exp);
    }

    @AssertTrue(message = "Invalid permit issuer or issued_for")
    private boolean isValid() {
        if (!this.getProducer().equals(this.permitIssuer)) {
            return false;
        }
        if (!this.getConsumer().equals(this.permitIssuedFor)) {
            return false;
        }
        return true;
    }
}
