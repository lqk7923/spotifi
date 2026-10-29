package axo.spotifyclone.spotifyclonebe.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiErrorWriter errorWriter;
    private final AuthenticationEntryPoint bearerDelegate = new BearerTokenAuthenticationEntryPoint();

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException ex
    ) throws IOException, ServletException {
        bearerDelegate.commence(request, response, ex);
        errorWriter.write(response, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication is required.");
    }
}