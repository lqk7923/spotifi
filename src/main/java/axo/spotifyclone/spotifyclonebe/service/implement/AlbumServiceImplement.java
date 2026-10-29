package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;
import axo.spotifyclone.spotifyclonebe.mapper.TrackResponseMapper;
import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.exceptions.ResourceNotFoundException;
import axo.spotifyclone.spotifyclonebe.persistent.Album;
import axo.spotifyclone.spotifyclonebe.repository.AlbumRepository;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImplement implements AlbumService {
    private final AlbumRepository albumRepository;
    private final MediaUrlService mediaUrlService;
    private final TrackResponseMapper trackResponseMapper;

    @Override
    @Transactional(readOnly = true)
    public AlbumDetailsResponse getAlbumData(UUID albumId){
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));
        PresignedUrl presignedCoverUrl = mediaUrlService.generateUrl(album.getAlbumCoverKey());
        return AlbumDetailsResponse.builder()
                .artist(album.getAlbumAuthor())
                .albumId(album.getAlbumId())
                .title(album.getAlbumTitle())
                .coverUrl(presignedCoverUrl.getUrl())
                .coverExpiresAt(presignedCoverUrl.getExpiration())
                .releaseDate(album.getAlbumReleaseDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackDetailsResponse> listAlbumTracks(UUID albumId){
        List<TrackDetailsProjection> tracks = albumRepository.findTrackDetailsByAlbumId(albumId);
        if(tracks.isEmpty()){
            throw new ResourceNotFoundException("Album", albumId);
        }
        return tracks.stream()
                .filter(track -> track.getTrackId() != null)
                .map(trackResponseMapper::toResponse)
                .toList();
    }
}
