package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.PaginationRequestFactory;
import axo.spotifyclone.spotifyclonebe.component.SearchQueryFactory;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;
import axo.spotifyclone.spotifyclonebe.mapper.TrackResponseMapper;
import axo.spotifyclone.spotifyclonebe.dto.response.AlbumDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.exceptions.ResourceNotFoundException;
import axo.spotifyclone.spotifyclonebe.persistent.Album;
import axo.spotifyclone.spotifyclonebe.repository.AlbumRepository;
import axo.spotifyclone.spotifyclonebe.service.AlbumService;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImplement implements AlbumService {
    private final AlbumRepository albumRepository;
    private final MediaUrlService mediaUrlService;
    private final TrackResponseMapper trackResponseMapper;
    private final PaginationRequestFactory paginationRequestFactory;
    private final SearchQueryFactory searchQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public AlbumDetailsResponse getAlbumData(UUID albumId){
        return albumRepository.findById(albumId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AlbumDetailsResponse> searchAlbums(String query, int page, int size){
        String pattern = searchQueryFactory.createPattern(query);
        return PageResponse.from(albumRepository.searchAlbums(pattern, paginationRequestFactory.create(page, size))
                .map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrackDetailsResponse> listAlbumTracks(UUID albumId, int page, int size){
        Page<TrackDetailsProjection> tracks = albumRepository.findTrackDetailsByAlbumId(
                albumId, paginationRequestFactory.create(page, size));
        if(tracks.getTotalElements() == 0 && !albumRepository.existsById(albumId)){
            throw new ResourceNotFoundException("Album", albumId);
        }
        return PageResponse.from(tracks.map(trackResponseMapper::toResponse));
    }

    private AlbumDetailsResponse toResponse(Album album){
        PresignedUrl presignedCoverUrl = mediaUrlService.generateUrl(album.getAlbumCoverKey());
        return AlbumDetailsResponse.builder()
                .artist(album.getAlbumAuthor())
                .albumId(album.getAlbumId())
                .title(album.getAlbumTitle())
                .coverUrl(presignedCoverUrl == null ? null : presignedCoverUrl.getUrl())
                .coverExpiresAt(presignedCoverUrl == null ? null : presignedCoverUrl.getExpiration())
                .releaseDate(album.getAlbumReleaseDate())
                .albumType(album.getAlbumType())
                .build();
    }
}
