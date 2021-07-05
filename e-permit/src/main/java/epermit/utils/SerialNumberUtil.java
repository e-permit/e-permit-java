package epermit.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.AuthorityIssuerQuota;
import epermit.entities.LedgerQuota;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitIdInput;
import epermit.models.inputs.CreateQrCodeInput;
import epermit.models.inputs.QuotaSufficientInput;
import epermit.models.valueobjects.AuthorityIssuerQuotaPayload;
import epermit.repositories.AuthorityIssuerQuotaRepository;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SerialNumberUtil {
    private final PermitUtil permitUtil;
    private final AuthorityIssuerQuotaRepository authorityIssuerQuotaRepository;

    public Optional<Integer> generate(String issuedFor, int py, PermitType pt) {
        AuthorityIssuerQuota quota = permitUtil.getIssuerQuota(issuedFor, pt, py);
        AuthorityIssuerQuotaPayload issuerQuotaPayload = quota.getPayload();
        // reuse revoked number
        if (!issuerQuotaPayload.getAvailableSerialNumbers().isEmpty()) {
            Integer n = issuerQuotaPayload.getAvailableSerialNumbers().get(0);
            issuerQuotaPayload.getAvailableSerialNumbers().remove(0);
            quota.setPayload(issuerQuotaPayload);
            authorityIssuerQuotaRepository.save(quota);
            return Optional.of(n);
        }
        LedgerQuota ledgerQuota = permitUtil.getActiveLedgerQuota(issuerQuotaPayload, issuedFor, pt, py);

        int nextSerialNumber = issuerQuotaPayload.getNextNumber();
        // disable active quota
        if (nextSerialNumber == ledgerQuota.getEndNumber()) {
            log.info("Quota founded");
            issuerQuotaPayload.setActiveQuotaId(null);
            issuerQuotaPayload.setNextNumber(null);
            issuerQuotaPayload.getUsedQuotaIds().add(ledgerQuota.getId());
        } else {
            log.info("Next serial number is valid", nextSerialNumber);
            issuerQuotaPayload.setNextNumber(nextSerialNumber + 1);
        }
        authorityIssuerQuotaRepository.save(quota);
        return Optional.of(nextSerialNumber);
    }

}
