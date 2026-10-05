package axo.spotifyclone.spotifyclonebe.dto.response;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDataProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class AlbumDataResponse {
    private List<TrackDataProjection> albumTracks;
    private String author;
    private String albumTitle;
    private UUID albumId;
    private String albumCoverPresignedUrl;
    private LocalDate releaseDate;
}
