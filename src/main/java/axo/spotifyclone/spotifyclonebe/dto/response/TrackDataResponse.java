package axo.spotifyclone.spotifyclonebe.dto.response;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class TrackDataResponse {
    private UUID trackId;
    private String title;
    private Long durationMs;
    private String artist;
    private String albumTitle;
    private UUID albumId;
    private String coverUrl;
    private Instant coverExpiresAt;
}
