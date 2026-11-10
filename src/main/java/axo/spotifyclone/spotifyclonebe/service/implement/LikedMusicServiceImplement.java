package axo.spotifyclone.spotifyclonebe.service.implement;

import axo.spotifyclone.spotifyclonebe.component.PaginationRequestFactory;
import axo.spotifyclone.spotifyclonebe.dto.response.PageResponse;
import axo.spotifyclone.spotifyclonebe.dto.response.TrackDetailsResponse;
import axo.spotifyclone.spotifyclonebe.exceptions.BadRequestException;
import axo.spotifyclone.spotifyclonebe.mapper.TrackResponseMapper;
import axo.spotifyclone.spotifyclonebe.repository.LikedMusicRepository;
import axo.spotifyclone.spotifyclonebe.service.LikedMusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikedMusicServiceImplement implements LikedMusicService {
    private final LikedMusicRepository likedMusicRepository;
    private final TrackResponseMapper trackResponseMapper;
    private final PaginationRequestFactory paginationRequestFactory;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TrackDetailsResponse> listLikedTracks(String userId, int page, int size){
        if(userId == null || userId.isBlank()){
            throw new BadRequestException("User ID must not be blank.");
        }
        return PageResponse.from(likedMusicRepository.findTrackDetailsByUserId(
                        userId, paginationRequestFactory.create(page, size))
                .map(trackResponseMapper::toResponse));
    }
}
