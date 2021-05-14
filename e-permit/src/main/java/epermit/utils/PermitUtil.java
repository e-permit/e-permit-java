package epermit.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import epermit.models.CreatePermitInput;
import epermit.models.EPermitProperties;
import epermit.models.IssuedPermit;
import epermit.models.PermitType;
import epermit.services.PermitService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final EPermitProperties properties;
    private final PermitService permitService;

    public String getPermitId(String iss, String aud, PermitType pt, Integer py, int serialNumber) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(iss).add(aud).add(Integer.toString(py))
                .add(Integer.toString(pt.getCode())).add(Long.toString(serialNumber)).toString();
        return permitId;
    }

    public IssuedPermit createPermit(CreatePermitInput input) {
        String issuer = properties.getIssuerCode();
        Integer serialNumber = permitService.getSerialNumber(input.getIssuedFor(),
                input.getPermitYear(), input.getPermitType());
        String permitId = getPermitId(issuer, input.getIssuedFor(), input.getPermitType(),
                input.getPermitYear(), serialNumber);
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName(input.getCompanyName());
        permit.setIssuedFor(input.getIssuedFor());
        permit.setPermitId(permitId);
        permit.setPermitType(input.getPermitType());
        permit.setPermitYear(input.getPermitYear());
        permit.setPlateNumber(input.getPlateNumber());
        permit.setSerialNumber(serialNumber);
        permit.setIssuedAt(createIssuedAt());
        permit.setExpireAt(createExpireDate(input.getPermitYear()));
        permit.setQrCode(generateQrCode(permit));
        permit.setClaims(input.getClaims());
        return permit;
    }

    private String generateQrCode(IssuedPermit permit) {
        String iss = properties.getIssuerCode();
        String aud = permit.getIssuedFor();
        String year = Integer.toString(permit.getPermitYear());
        String pt = Integer.toString(permit.getPermitType().getCode());
        String pid = Integer.toString(permit.getSerialNumber());
        String iat = permit.getIssuedAt();
        String exp = permit.getExpireAt();
        String sub = permit.getPlateNumber();
        String cn = permit.getCompanyName();
        String payload = String.join("#", iss, aud, year, pt, pid, iat, exp, sub, cn);
        return jwsUtil.createJws(payload);
    }

    private String createExpireDate(int year) {
        String expireDate = "30/01/" + Integer.toString(year + 1);
        return expireDate;
    }

    private String createIssuedAt() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return OffsetDateTime.now().format(dtf);
    }
}
