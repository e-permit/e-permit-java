package epermit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import epermit.services.UserService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().anyRequest().authenticated().and()
                .httpBasic();
        http.userDetailsService(userService);
        return http.build();
    }

    /*@Bean
    public UserDetailsService  userDetailsService() {
        return userService;
        List<UserDetails> users = new ArrayList<>();
        UserDetails admin = User.withUsername("admin").password("{noop}" + adminPassword)
                .roles("ADMIN").build();
        users.add(admin);
        return new InMemoryUserDetailsManager(users);
    }*/
    
}
