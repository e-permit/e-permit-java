package epermit.ledgerevents.permitcreated;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import epermit.commons.Constants;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCreatedLedgerEvent extends LedgerEventBase {
    public PermitCreatedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.PERMIT_CREATED);
    }

    @NotNull
    @Pattern(regexp = Constants.PERMIT_ID_FORMAT)
    private String permitId;

    @NotNull
    private Integer permitType;

    @NotNull
    private String permitIssuer;

    @NotNull
    private String permitIssuedFor;

    @NotNull
    @Min(2021)
    private int permitYear;

    @NotNull
    @Pattern(regexp = Constants.DATE_FORMAT)
    private String issuedAt;

    @NotNull
    @Pattern(regexp = Constants.DATE_FORMAT)
    private String expiresAt;

    private String plateNumber;

    private String plateNumber2;

    @NotNull
    private String companyName;

    @NotNull
    private String companyId;

    @NotNull
    private String departureCountry;

    @NotNull
    private String arrivalCountry;

    @NotNull
    private String qrCode;

    private Map<String, Object> otherClaims;
    
    @AssertTrue(message = "Invalid issued_at")
    private boolean isValidIssuedAt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate iat = LocalDate.parse(this.issuedAt, dtf);
        return iat.isBefore(LocalDate.now(ZoneOffset.UTC))
                || iat.equals(LocalDate.now(ZoneOffset.UTC));
    }

    @AssertTrue(message = "Invalid expires_at")
    private boolean isValidExpiresAt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate iat = LocalDate.parse(this.issuedAt, dtf);
        LocalDate exp = LocalDate.parse(this.expiresAt, dtf);
        return iat.isBefore(exp);
    }

    @AssertTrue(message = "Invalid permit issuer or issued_for")
    private boolean isValid() {
        if (!this.getEventProducer().equals(this.permitIssuer)) {
            return false;
        }
        if (!this.getEventConsumer().equals(this.permitIssuedFor)) {
            return false;
        }
        return true;
    }
}
