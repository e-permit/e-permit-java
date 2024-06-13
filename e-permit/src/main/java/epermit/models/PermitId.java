package epermit.models;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import epermit.commons.Constants;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

@Data
@Builder(toBuilder = true)
public class PermitId {
    private String issuer;
    private String issuedFor;
    private Integer permitType;
    private Integer permitYear;
    private Long serialNumber;

    @SneakyThrows
    public static PermitId parse(String id) {
        var permitId = PermitId.builder();
        Pattern pattern = Pattern.compile(Constants.PERMIT_ID_FORMAT);
        Matcher matcher = pattern.matcher(id);
        if (!matcher.matches()) {
            throw new Exception();
        }
        String[] parts = id.split("-");
        permitId.issuer(parts[0]);
        permitId.issuedFor(parts[1]);
        permitId.permitYear(Integer.parseInt(parts[2]));
        permitId.permitType(Integer.parseInt(parts[3]));
        permitId.serialNumber(Long.parseLong(parts[4]));
        return permitId.build();
    }

    @SneakyThrows
    public String toString() {
        StringJoiner joiner = new StringJoiner("-");
        joiner.add(this.getIssuer()).add(this.getIssuedFor())
                .add(Integer.toString(this.getPermitYear())).add(this.getPermitType().toString())
                .add(Long.toString(this.getSerialNumber()));
        return joiner.toString();
    }
}
