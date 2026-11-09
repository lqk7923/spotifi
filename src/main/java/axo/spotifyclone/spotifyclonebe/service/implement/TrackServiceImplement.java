package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.PaginationRequestFactory;
import axo.spotifyclone.spotifyclonebe.component.SearchQueryFactory;
import axo.spotifyclone.spotifyclonebe.dto.storage.PresignedUrl;
import axo.spotifyclone.spotifyclonebe.mapper.TrackResponseMapper;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackDetailsProjection;
import axo.spotifyclone.spotifyclonebe.dto.projection.TrackPlaybackSourceProjection;
import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackPlaybackResponse;
import axo.spotifyclone.spotifyclonebe.exceptions.BadRequestException;
import axo.spotifyclone.spotifyclonebe.exceptions.CustomException;
import axo.spotifyclone.spotifyclonebe.exceptions.ResourceNotFoundException;
import axo.spotifyclone.spotifyclonebe.repository.TrackRepository;
import axo.spotifyclone.spotifyclonebe.service.MediaUrlService;
import axo.spotifyclone.spotifyclonebe.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackServiceImplement implements TrackService {
    private final MediaUrlService mediaUrlService;
    private final TrackRepository trackRepository;
    private final TrackResponseMapper trackResponseMapper;
    private final PaginationRequestFactory paginationRequestFactory;
    private final SearchQueryFactory searchQueryFactory;

    @Override
    @Transactional(readOnly = true)
    public TrackPlaybackResponse getPlayback(UUID trackId, String include){
        boolean isIncludeMetaData = isIncludeMetaData(include);

        if(isIncludeMetaData) {
            TrackDetailsProjection track = trackRepository.findDetailsByTrackId(trackId)
                    .orElseThrow(() -> new ResourceNotFoundException("Track", trackId));
            return buildPlaybackResponse(trackId, track.getTrackKey(), trackResponseMapper.toResponse(track));
        }

        TrackPlaybackSourceProjection track = trackRepository.findPlaybackSourceByTrackId(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track", trackId));
        return buildPlaybackResponse(trackId, track.getTrackKey(), null);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackDetailsResponse getTrackData(UUID trackId){
        return trackRepository.findDetailsByTrackId(trackId)
                .map(trackResponseMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Track", trackId));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrackDetailsResponse> searchTracks(String query, int page, int size){
        String pattern = searchQueryFactory.createPattern(query);
        return PageResponse.from(trackRepository.searchDetails(pattern, paginationRequestFactory.create(page, size))
                .map(trackResponseMapper::toResponse));
    }

    private TrackPlaybackResponse buildPlaybackResponse(UUID trackId, String trackKey, TrackDetailsResponse metadata){
        if(trackKey == null || trackKey.isBlank()){
            throw new CustomException("Track " + trackId + " has no audio source.");
        }

        PresignedUrl presignedTrackUrl = mediaUrlService.generateUrl(trackKey);

        return TrackPlaybackResponse.builder()
                .trackId(trackId)
                .audioUrl(presignedTrackUrl.getUrl())
                .expiresAt(presignedTrackUrl.getExpiration())
                .metadata(metadata)
                .build();
    }

    private boolean isIncludeMetaData(String include){
        if(include == null || include.isEmpty()) return false;
        if (include.equals("metadata")) return true;
        throw new BadRequestException("Include must be omitted or set to 'metadata'.");
    }
}
