package axo.spotifyclone.spotifyclonebe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class AlbumInfoResponse {
    private List<TrackInfoProjection> albumTracks;
    private String author;
    private String albumTitle;
    private UUID albumId;
}
