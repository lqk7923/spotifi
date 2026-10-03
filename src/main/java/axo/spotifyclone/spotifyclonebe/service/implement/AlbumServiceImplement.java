package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.dto.projection.AlbumInfoResponse;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackFullInfoProjection;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackInfoProjection;
import axo.spotifyclone.spotifyclonebe.repository.AlbumRepository;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AlbumServiceImplement implements AlbumService {
    private final AlbumRepository albumRepository;

    @Override
    public AlbumInfoResponse getAlbumData(UUID albumId) {
        List<TrackFullInfoProjection> trackFullInfos = albumRepository.findByAlbumId(albumId);
        if(trackFullInfos.isEmpty())
            throw new RuntimeException("Album isn't exist");
        TrackFullInfoProjection firstTrackFullInfo = trackFullInfos.getFirst();
        return AlbumInfoResponse.builder()
                .author(firstTrackFullInfo.getAuthor())
                .albumId(firstTrackFullInfo.getAlbumId())
                .albumTitle(firstTrackFullInfo.getAlbumTitle())
                .albumTracks(trackFullInfos
                        .stream()
                        .map(trackFullInfo -> TrackInfoProjection.builder()
                                        .bucketName(trackFullInfo.getBucketName())
                                        .trackDuration(trackFullInfo.getTrackDuration())
                                        .trackId(trackFullInfo.getTrackId())
                                        .trackTitle(trackFullInfo.getTrackTitle())
                                        .build()
                        )
                        .toList()
                ).build();
    }
}
