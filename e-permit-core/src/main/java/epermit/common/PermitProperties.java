package epermit.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "epermit")
public class PermitProperties {

    private String keyPassword;

    private String issuerCode;

    private String issuerTitle;

    private String issuerApiUri;

    private String issuerVerifyUri;
}
