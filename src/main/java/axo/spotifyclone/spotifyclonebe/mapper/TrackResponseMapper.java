package axo.spotifyclone.spotifyclonebe.mapper;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TrackResponseMapper {
    private final MediaUrlService mediaUrlService;

    public TrackDetailsResponse toResponse(TrackDetailsProjection track){
        Instant expiresAt = mediaUrlService.getExpiresAt();
        String coverUrl = mediaUrlService.generateUrl(track.getAlbumCoverKey());
        return TrackDetailsResponse.builder()
                .trackId(track.getTrackId())
                .title(track.getTrackTitle())
                .durationMs(track.getTrackDuration())
                .artist(track.getAuthor())
                .albumTitle(track.getAlbumTitle())
                .albumId(track.getAlbumId())
                .coverUrl(coverUrl)
                .coverExpiresAt(coverUrl == null ? null : expiresAt)
                .build();
    }
}
