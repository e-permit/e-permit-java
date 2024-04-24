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
                .title(env.getProperty("e-permit"))
                .version(buildProperties.getVersion())
                .description(env.getProperty("e-permit internal api"));

        return new OpenAPI().info(info);
    }

}
