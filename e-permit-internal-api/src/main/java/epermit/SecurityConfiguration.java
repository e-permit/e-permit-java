package epermit;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    @Value("${epermit.admin-password}")
    private String adminPassword;

    @Autowired
    private Environment env;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().anyRequest().authenticated().and()
                .httpBasic();

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        List<UserDetails> users = new ArrayList<>();
        UserDetails admin = User.withUsername("admin").password("{noop}" + adminPassword)
                .roles("ADMIN").build();
        users.add(admin);
        String verPwd = env.getProperty("EPERMIT_VERIFIER_PASSWORD");
        if(verPwd != null){
            UserDetails verifier = User.withUsername("verifier").password("{noop}" + verPwd)
                .roles("VERIFIER").build();
            users.add(verifier);
        }
        return new InMemoryUserDetailsManager(users);
    }
}
