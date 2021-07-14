package epermit.services;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Authority;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.enums.AuthenticationType;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.AuthorityRepository;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersistedEventService {
    private final JwsUtil jwsUtil;
    private final LedgerEventUtil ledgerEventUtil;
    private final AuthorityRepository authorityRepository;

    @Transactional
    public LedgerEventHandleResult handleEvent(Map<String, Object> claims, String proof) {
        Authority authority = authorityRepository.findOneByCode(claims.get("event_issuer").toString());
        if(authority.getAuthenticationType() == AuthenticationType.BASIC){
            
        }else{
            JwsValidationResult jwsValidationResult = jwsUtil.validateJws(proof);
            if (!jwsValidationResult.isValid()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Jws validation error");
            }
        }
        return ledgerEventUtil.handleEvent(claims);
    }

    @Transactional
    public void sendEvent(){
        
    }
}

