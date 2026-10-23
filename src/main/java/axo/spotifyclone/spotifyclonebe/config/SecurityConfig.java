package axo.spotifyclone.spotifyclonebe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.net.http.HttpClient;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
        http.authorizeHttpRequests(
                (request -> request
                        .requestMatchers("/actuator/health","/swagger-ui/**", "/v3/api-docs*/**").permitAll()
                        .anyRequest().authenticated())
        );
        return http.build();
    }
}
