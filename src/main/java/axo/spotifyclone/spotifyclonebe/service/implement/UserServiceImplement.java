package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.dto.response.UserResponse;
import axo.spotifyclone.spotifyclonebe.repository.UserRepository;
import axo.spotifyclone.spotifyclonebe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void synchronize(Jwt jwt){
        String id = jwt.getSubject();
        if(id == null || id.isBlank() || id.length() > 255){
            throw new InvalidBearerTokenException("Token must contain a valid subject.");
        }
        userRepository.insertIfAbsent(id, jwt.getClaimAsString("preferred_username"), jwt.getClaimAsString("email"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(String id){
        var user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Authenticated user has not been synchronized."));
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
