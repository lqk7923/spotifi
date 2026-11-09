package axo.spotifyclone.spotifyclonebe.config;

import axo.spotifyclone.spotifyclonebe.component.UserSynchronizationFilter;
import axo.spotifyclone.spotifyclonebe.exceptions.ApiErrorWriter;
import axo.spotifyclone.spotifyclonebe.service.UserService;
import axo.spotifyclone.spotifyclonebe.exceptions.ApiAccessDeniedHandler;
import axo.spotifyclone.spotifyclonebe.exceptions.ApiAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
    private final ApiAccessDeniedHandler apiAccessDeniedHandler;
    private final UserService userService;
    private final ApiErrorWriter apiErrorWriter;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new UserSynchronizationFilter(userService, apiAuthenticationEntryPoint, apiErrorWriter),
                        BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/actuator/health").permitAll()//health
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs*/**", "/swagger-ui.html").permitAll()//swagger-ui
                        .requestMatchers(HttpMethod.GET, "/tracks").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler))
                .oauth2ResourceServer(oauth -> oauth
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler)
                        .jwt(Customizer.withDefaults()))
                .build();
    }
}
