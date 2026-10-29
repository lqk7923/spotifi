package axo.spotifyclone.spotifyclonebe.dto.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class PresignedUrl {
    private String url;
    Instant expiration;
}
