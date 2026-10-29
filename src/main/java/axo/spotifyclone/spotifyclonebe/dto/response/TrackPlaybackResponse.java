package axo.spotifyclone.spotifyclonebe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TrackPlaybackResponse {
    private UUID trackId;
    private String audioUrl;
    private Instant expiresAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TrackDetailsResponse metadata;
}
