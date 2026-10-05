package axo.spotifyclone.spotifyclonebe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class TrackDataProjection {
    private UUID trackId;
    private String trackTitle;
    private Long trackDuration;
}