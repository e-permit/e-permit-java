package epermit.services;

import epermit.events.keycreated.KeyCreatedEvent;
import epermit.events.quotacreated.QuotaCreatedEvent;
import epermit.models.CreateAuthorityInput;
import epermit.models.CreateQuotaInput;

public interface AuthorityService {
    boolean isPublicKeyExist(String authorityCode, String keyId);
    String getPublicKeyJwk(String authorityCode, String keyId);
    boolean isQuotaSufficient();
    void createAuthority(CreateAuthorityInput input);
    void createQuota(CreateQuotaInput input);
    void handleQuotaCreated(QuotaCreatedEvent event);
    void handleKeyCreated(KeyCreatedEvent event);
}
