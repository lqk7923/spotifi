package axo.spotifyclone.spotifyclonebe.dto.response;

import axo.spotifyclone.spotifyclonebe.persistent.AlbumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class AlbumDetailsResponse {
    private UUID albumId;
    private String title;
    private String artist;
    private String coverUrl;
    private Instant coverExpiresAt;
    private LocalDate releaseDate;
    private AlbumType albumType;
}
