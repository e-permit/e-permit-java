package epermit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Authority;
import epermit.entities.AuthorityIssuerQuota;
import epermit.entities.LedgerQuota;
import epermit.models.EPermitProperties;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityIssuerQuotaRepository;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SerialNumberUtil {
    private final AuthorityRepository authorityRepository;

    private final AuthorityIssuerQuotaRepository authorityIssuerQuotaRepository;

    private final LedgerQuotaRepository ledgerQuotaRepository;

    private final EPermitProperties properties;

    public Integer generate(String issuedFor, int py, PermitType pt) {
        Authority authority = authorityRepository.findOneByCode(issuedFor);
        Integer nextSerialNumber;
        AuthorityIssuerQuota quota = authority.getIssuerQuota(pt, py);
        if (!quota.getRevokedSerialNumbers().isEmpty()) {
            nextSerialNumber = quota.getRevokedSerialNumbers().remove(0);
        } else if (quota.getNextNumber() != null) {
            nextSerialNumber = quota.getNextNumber();
            if (nextSerialNumber == quota.getEndNumber()) {
                quota.setStartNumber(null);
                quota.setEndNumber(null);
                quota.setNextNumber(null);
            }
        } else {
            LedgerQuota ledgerQuota = getLedgerQuota(issuedFor, pt, py, quota.getUsedLedgerQuotaIds());
            quota.getUsedLedgerQuotaIds().add(ledgerQuota.getId());
            quota.setStartNumber(ledgerQuota.getStartNumber());
            quota.setEndNumber(ledgerQuota.getEndNumber());
            quota.setNextNumber(ledgerQuota.getStartNumber());
            nextSerialNumber = quota.getNextNumber();
        }
        authorityIssuerQuotaRepository.save(quota);
        return nextSerialNumber;
    }

    private LedgerQuota getLedgerQuota(String issuedFor, PermitType pt, int py,
            List<Integer> usedIds) {
        Optional<LedgerQuota> ledgerQuota = ledgerQuotaRepository.findAll().stream()
                .filter(x -> x.getPermitIssuer().equals(properties.getIssuerCode())
                        && x.getPermitIssuedFor().equals(issuedFor) && x.getPermitType() == pt
                        && x.getPermitYear() == py && !usedIds.contains(x.getId()))
                .findFirst();
        if (ledgerQuota.isPresent()) {
            return ledgerQuota.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quota is not available");
        }
    }

}
