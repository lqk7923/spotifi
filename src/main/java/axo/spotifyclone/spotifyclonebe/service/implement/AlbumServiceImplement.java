package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.CloudflareR2Client;
import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDataResponse;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullDataProjection;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDataProjection;
import axo.spotifyclone.spotifyclonebe.repository.AlbumRepository;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AlbumServiceImplement implements AlbumService {
    private final AlbumRepository albumRepository;
    private final CloudflareR2Client cloudflareR2Client;

    @Override
    public AlbumDataResponse getAlbumData(UUID albumId) {
        List<TrackFullDataProjection> listTrackData = albumRepository.findByAlbumId(albumId);
        if(listTrackData.isEmpty())
            throw new RuntimeException("Album isn't exist");
        TrackFullDataProjection firstTrackFullInfo = listTrackData.getFirst();
        return AlbumDataResponse.builder()
                .author(firstTrackFullInfo.getAuthor())
                .albumId(firstTrackFullInfo.getAlbumId())
                .albumTitle(firstTrackFullInfo.getAlbumTitle())
                .albumCoverPresignedUrl(cloudflareR2Client.presignedGetObjectRequest(Duration.ofMinutes(2),firstTrackFullInfo.getAlbumCoverKey()))
                .albumTracks(listTrackData
                        .stream()
                        .map(trackFullInfo -> TrackDataProjection.builder()
                                        .trackDuration(trackFullInfo.getTrackDuration())
                                        .trackId(trackFullInfo.getTrackId())
                                        .trackTitle(trackFullInfo.getTrackTitle())
                                        .build()
                        ).toList())
                .releaseDate(firstTrackFullInfo.getAlbumReleaseDate())
                .build();
    }
}
