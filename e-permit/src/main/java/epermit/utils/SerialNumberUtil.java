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
        Gson gson = GsonUtil.getGson();
        Authority authority = authorityRepository.findOneByCode(issuedFor);
        Integer nextSerialNumber;
        AuthorityIssuerQuota quota;
        Optional<AuthorityIssuerQuota> quotaR = authority.getIssuerQuota(pt, py);
        if (!quotaR.isPresent()) {
            quota = new AuthorityIssuerQuota();
            quota.setPermitType(pt);
            quota.setPermitYear(py);
            quota.setAvailableSerialNumbers(gson.toJson(List.of()));
            quota.setUsedLedgerQuotaIds(gson.toJson(List.of()));
        } else {
            quota = quotaR.get();
        }
        List<Integer> availableSerialNumbers = new ArrayList<>(
                Arrays.asList(gson.fromJson(quota.getAvailableSerialNumbers(), Integer[].class)));
        List<Integer> usedLedgerQuotaIds = new ArrayList<>(
                Arrays.asList(gson.fromJson(quota.getUsedLedgerQuotaIds(), Integer[].class)));
        if (!availableSerialNumbers.isEmpty()) {
            nextSerialNumber = availableSerialNumbers.remove(0);
        } else if (quota.getNextNumber() != null) {
            nextSerialNumber = quota.getNextNumber();
            if (nextSerialNumber == quota.getNextNumber()) {
                quota.setStartNumber(null);
                quota.setEndNumber(null);
                quota.setNextNumber(null);
            }
        } else {
            LedgerQuota ledgerQuota = getLedgerQuota(issuedFor, pt, py, usedLedgerQuotaIds);
            usedLedgerQuotaIds.add(ledgerQuota.getId());
            quota.setStartNumber(ledgerQuota.getStartNumber());
            quota.setEndNumber(ledgerQuota.getEndNumber());
            quota.setNextNumber(ledgerQuota.getStartNumber());
            nextSerialNumber = quota.getNextNumber();
        }
        quota.setAvailableSerialNumbers(gson.toJson(availableSerialNumbers));
        quota.setUsedLedgerQuotaIds(gson.toJson(usedLedgerQuotaIds));
        authorityIssuerQuotaRepository.save(quota);
        return nextSerialNumber;
    }

    private LedgerQuota getLedgerQuota(String issuedFor, PermitType pt, int py,
            List<Integer> usedIds) {
        Optional<LedgerQuota> ledgerQuota = ledgerQuotaRepository.findAll().stream()
                .filter(x -> x.getIssuer().equals(properties.getIssuerCode())
                        && x.getIssuedFor().equals(issuedFor) && x.getPermitType() == pt
                        && x.getPermitYear() == py && !usedIds.contains(x.getId()))
                .findFirst();
        if (ledgerQuota.isPresent()) {
            return ledgerQuota.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quota is not available");
        }
    }

}
