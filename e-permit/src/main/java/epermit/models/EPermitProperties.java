package epermit.models;

import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "epermit")
public class EPermitProperties {

    private String issuerCode;

    private String issuerName;

    private String keystorePassword;

    private Optional<String> xroadClientId;

    private Optional<String> xroadUrl;

}
