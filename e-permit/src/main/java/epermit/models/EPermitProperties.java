package epermit.models;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "epermit")
public class EPermitProperties {

    private String keyPassword;

    private String issuerCode;

    private String issuerTitle;

    private String issuerApiUri;

    private String issuerVerifyUri; 

    private String issuerPrivateKey;

    private String qrcodeVersion;
}