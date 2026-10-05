package axo.spotifyclone.spotifyclonebe.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class TrackFullDataProjection {
    private UUID trackId;
    private String trackTitle;
    private Long trackDuration;
    private String author;
    private String albumTitle;
    private UUID albumId;
    private String albumCoverKey;
    private LocalDate albumReleaseDate;
}