package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.CloudflareR2Client;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDataResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPresignedLink;
import axo.spotifyclone.spotifyclonebe.repository.TrackRepository;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TrackServiceImplement implements TrackService {

    private final CloudflareR2Client cloudflareR2Client;
    private final TrackRepository trackRepository;

    public TrackPresignedLink generatePresignedDownloadUrl(UUID objectId, Duration expiration) {
        TrackInfoProjection objectKey = trackRepository
                .findTrackKeyById(objectId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        return TrackPresignedLink.builder()
                .coverPresignedLink(cloudflareR2Client.presignedGetObjectRequest(expiration, objectKey.getCoverKey()))
                .trackPresignedLink(cloudflareR2Client.presignedGetObjectRequest(expiration, objectKey.getTrackKey()))
                .build();
    }

    /**
     * Lists all track's information in system
     */
    @Override
    public List<TrackDataResponse> listTracksInfo() {
        return trackRepository.findAllTracksInfo()
                .stream()
                .map(track -> TrackDataResponse.builder()
                        .trackId(String.valueOf(track.getTrackId()))
                        .trackDuration(track.getTrackDuration())
                        .trackTitle(track.getTrackTitle())
                        .author(track.getAuthor())
                        .albumTitle(track.getAlbumTitle())
                        .albumId(String.valueOf(track.getAlbumId()))
                        .coverPresignedUrl(cloudflareR2Client.presignedGetObjectRequest(Duration.ofMinutes(2), track.getAlbumCoverKey()))
                        .build())
                .toList();
    }
}
