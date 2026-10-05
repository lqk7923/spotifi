package axo.spotifyclone.spotifyclonebe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TrackInfoProjection {
    private String trackKey;
    private String coverKey;
}