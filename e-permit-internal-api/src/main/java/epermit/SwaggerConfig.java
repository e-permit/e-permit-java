package epermit;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private final BuildProperties buildProperties;
    private final Environment env;

    @Bean
    public OpenAPI uosOpenAPI() {

        Info info = new Info()
                .title("E-Permit Internal API(" + env.getProperty("EPERMIT_ISSUER_NAME") + ")")
                .version(buildProperties.getVersion())
                .description("""
                 APIs for managing authorities, quotas, keys, permits, and health checks for the e-permit system.
        
                    - 200: Success
                    - 400: Bad Request
                    - 401: Unauthorized
                    - 404: Not found
                    - 422: Validation Error
                    - 500: Internal Server Error      
                        """);

        return new OpenAPI().info(info);
    }

}
