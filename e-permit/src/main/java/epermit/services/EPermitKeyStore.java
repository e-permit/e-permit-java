package epermit.services;

import java.util.List;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;

import epermit.models.dtos.PublicJwk;

public interface EPermitKeyStore {
    List<PublicJwk> getKeys();
    String sign(String keyId, Payload payload, JWSHeader header);
}
