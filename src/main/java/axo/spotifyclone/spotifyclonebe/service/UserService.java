package axo.spotifyclone.spotifyclonebe.service;

import axo.spotifyclone.spotifyclonebe.dto.response.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {
    void synchronize(Jwt jwt);
    UserResponse getUser(String id);
}
