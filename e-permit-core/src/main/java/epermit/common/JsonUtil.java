package epermit.common;

import java.util.Map;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import lombok.SneakyThrows;

public class JsonUtil {
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T getClaim(String jws, String key) {
        JWSObject jwsObject = JWSObject.parse(jws);
        return (T) jwsObject.getPayload().toJSONObject().get(key);
    }

    public static Gson getGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @SneakyThrows
    public static String createDummyJws(Map<String, Object> claims) {
        ECKey ecJWK = new ECKeyGenerator(Curve.P_256).keyID("123").generate();
        JWSSigner signer = new ECDSASigner(ecJWK);
        JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(ecJWK.getKeyID()).build(),
                new Payload(getGson().toJson(claims)));
        jwsObject.sign(signer);
        String s = jwsObject.serialize();
        return s;
    }

}
