package epermit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import epermit.entities.Authority;
import epermit.entities.AuthorityIssuerQuota;
import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityIssuerQuotaRepository;
import epermit.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SerialNumberUtil {
    private final PermitUtil permitUtil;

    private final AuthorityRepository authorityRepository;

    private final AuthorityIssuerQuotaRepository authorityIssuerQuotaRepository;

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
        List<Integer> availableSerialNumbers = new ArrayList<>(Arrays.asList(
                gson.fromJson(quota.getAvailableSerialNumbers(), Integer[].class)));
        List<Integer> usedLedgerQuotaIds = new ArrayList<>(Arrays.asList(
            gson.fromJson(quota.getUsedLedgerQuotaIds(), Integer[].class)));
        if (!availableSerialNumbers.isEmpty()) {
            nextSerialNumber = availableSerialNumbers.remove(0);
        }else if(quota.getNextNumber() != null){
            nextSerialNumber =  quota.getNextNumber(); 
            if(nextSerialNumber == quota.getNextNumber()){
                quota.setStartNumber(null);
                quota.setEndNumber(null);
                quota.setNextNumber(null);
            }
        }else{
            LedgerQuota ledgerQuota = permitUtil.getLedgerQuota(issuedFor, pt, py, usedLedgerQuotaIds);
            usedLedgerQuotaIds.add(ledgerQuota.getId());
            quota.setStartNumber(ledgerQuota.getStartNumber());
            quota.setEndNumber(ledgerQuota.getEndNumber());
            quota.setNextNumber(ledgerQuota.getStartNumber());
            nextSerialNumber =  quota.getNextNumber(); 
        }
        quota.setAvailableSerialNumbers(gson.toJson(availableSerialNumbers));
        quota.setUsedLedgerQuotaIds(gson.toJson(usedLedgerQuotaIds));
        authorityIssuerQuotaRepository.save(quota);
        return nextSerialNumber;
    }

}
