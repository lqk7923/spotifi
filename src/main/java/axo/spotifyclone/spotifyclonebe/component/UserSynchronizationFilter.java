package axo.spotifyclone.spotifyclonebe.component;

import axo.spotifyclone.spotifyclonebe.exceptions.ApiAuthenticationEntryPoint;
import axo.spotifyclone.spotifyclonebe.exceptions.ApiErrorWriter;
import axo.spotifyclone.spotifyclonebe.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class UserSynchronizationFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    private final ApiErrorWriter errorWriter;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken token && token.isAuthenticated()){
            try {
                userService.synchronize(token.getToken());
            } catch(AuthenticationException exception){
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, exception);
                return;
            } catch(DataAccessException exception){
                log.error("User synchronization failed", exception);
                errorWriter.write(response, HttpStatus.INTERNAL_SERVER_ERROR,
                        "INTERNAL_SERVER_ERROR", "An unexpected error occurred.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
