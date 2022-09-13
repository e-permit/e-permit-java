package epermit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;
import epermit.models.EPermitProperties;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.models.inputs.CreateQrCodeInput;
import epermit.models.inputs.QuotaSufficientInput;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUtil {
    private final JwsUtil jwsUtil;
    private final LedgerQuotaRepository quotaRepository;
    private final EPermitProperties properties;

    public String getPermitId(CreatePermitIdInput input) {
        StringJoiner joiner = new StringJoiner("-");
        String permitId = joiner.add(input.getIssuer()).add(input.getIssuedFor())
                .add(Integer.toString(input.getPermitYear())).add(input.getPermitType().getCode())
                .add(Integer.toString(input.getSerialNumber())).toString();
        return permitId;
    }

    public boolean isQuotaSufficient(QuotaSufficientInput input) {
        Boolean r = quotaRepository.findAll().stream()
                .anyMatch(x -> x.getPermitIssuer().equals(input.getIssuer())
                        && x.getPermitIssuedFor().equals(input.getIssuedFor()) && x.isActive()
                        && x.getPermitType() == input.getPermitType()
                        && input.getSerialNumber() >= x.getStartNumber()
                        && input.getSerialNumber() <= x.getEndNumber());
        log.info("isQuotaSufficient rusult is {}", r);
        return r;
    }

    public String generateQrCode(CreateQrCodeInput input) {
        log.info("generateQrCode started with {}", input);

        Map<String, String> claims = new HashMap<>();
        claims.put("id", input.getId());
        claims.put("iat", input.getIssuedAt());
        claims.put("exp", input.getExpireAt());
        claims.put("pn", input.getPlateNumber());
        claims.put("cn", input.getCompanyName());
        String jws = jwsUtil.createJws(claims);
        String qrCode = properties.getQrcodeVersion() + "." + jws;
        log.info("generateQrCode ended jws is {}", jws);

        return qrCode;
    }
}
