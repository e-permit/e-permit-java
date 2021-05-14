package epermit.services;

import epermit.models.PrivateKey;

public interface KeyService {
    boolean isExist(String keyId);
    void createKey(String keyId);
    PrivateKey getActiveKey();
}
