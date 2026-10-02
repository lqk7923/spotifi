package axo.spotifyclone.spotifyclonebe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class TrackInfoProjection {
    private String bucketName;
    private UUID trackId;
    private String trackTitle;
    private Long trackDuration;
    private String author;
    private String albumTitle;
    private UUID albumId;
}